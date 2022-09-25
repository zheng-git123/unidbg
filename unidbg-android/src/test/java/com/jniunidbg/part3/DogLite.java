package com.jniunidbg.part3;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.util.Base64;

public class DogLite extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;
    private final DvmClass NativeClass;

    public DogLite() {
        emulator = AndroidEmulatorBuilder.for32Bit().build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/lession3/app-debug.apk"));
        vm.setVerbose(true);
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("doglite", true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        NativeClass = vm.resolveClass("com/example/doglite/MainActivity");
    }

    public void callSysInfo() {
        NativeClass.newObject(null).callJniMethod(emulator, "SysInfo()V");
    }

    public void callDetectFile() {
        NativeClass.newObject(null).callJniMethod(emulator, "detectFile()V");
    }

    public void callDetectFileNew() {
        NativeClass.newObject(null).callJniMethod(emulator, "detectFileNew()V");
    }

    public void callgetAppFilesDir() {
        NativeClass.newObject(null).callJniMethod(emulator, "getAppFilesDir()V");
    }

    public void callBase64result() {
        NativeClass.newObject(null).callJniMethod(emulator, "base64result(Ljava/lang/String)V", new StringObject(vm, "12345"));
    }


    public static void main(String[] args) {
        DogLite lession3 = new DogLite();
        lession3.callgetAppFilesDir();
        //lession3.callDetectFile();
        //lession3.callSysInfo();
        //lession3.callDetectFileNew();
        //lession3.callBase64result();

    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/io/File-><init>(Ljava/lang/String;)V": {
                String pathName = vaList.getObjectArg(0).getValue().toString();
                emulator.set(dvmObject.toString(), pathName);
                return;
            }
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "android/app/ActivityThread->getApplication()Landroid/app/Application;": {
                return vm.resolveClass("android/app/Application").newObject(null);
            }
            case "java/io/File->getAbsolutePath()Ljava/lang/String;": {
                String methodName = dvmObject.getValue().toString();
                if (methodName.equals("android/os/Environment->getExternalStorageDirectory()Ljava/io/File;")) {
                    return new StringObject(vm, "ExternalStorageDirectory");
                } else if (methodName.equals("android/os/Environment->getStorageDirectory()Ljava/io/File;")) {
                    return new StringObject(vm, "StorageDirectory");
                }
            }

        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    /**
     * 方法签名
     *
     * @param vm
     * @param dvmClass
     * @param signature
     * @param vaList
     * @return
     */
    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "android/util/Base64->encodeToString([BI)Ljava/lang/String;":
                byte[] bytes = (byte[]) vaList.getObjectArg(0).getValue();
                //int flag = (int) vaList.getObjectArg(1).getValue();
                return new StringObject(vm, Base64.getEncoder().encodeToString(bytes));
            case "android/provider/Settings$Secure->getString(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;": {
                return new StringObject(vm, "123");
            }
            case "android/os/Environment->getExternalStorageDirectory()Ljava/io/File;": {
                return vm.resolveClass("java/io/File").newObject(signature);
            }
            case "android/os/Environment->getStorageDirectory()Ljava/io/File;": {
                return vm.resolveClass("java/io/File").newObject(signature);
            }
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/io/File-><init>(Ljava/lang/String;)V": {
                String pathName = vaList.getObjectArg(0).getValue().toString();
                System.out.println(pathName);
                return dvmClass.newObject(pathName);
            }
        }
        return super.newObjectV(vm, dvmClass, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/io/File->exists()Z": {
                String tag = emulator.get(dvmObject.toString());
                if ("/sys/class/power_supply/battery/voltage_now".equals(tag)) {
                    return true;
                } else if ("/data/local/tmp/nox".equals(tag)) {
                    return false;
                }

            }
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> allocObject(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "java/io/File->allocObject": {
                return dvmClass.newObject(null);
            }
        }
        return super.allocObject(vm, dvmClass, signature);
    }
}
