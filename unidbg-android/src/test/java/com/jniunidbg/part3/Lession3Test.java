package com.jniunidbg.part3;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.memory.Memory;

import java.io.File;

/**
 *
 */
public class Lession3Test extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;


    public Lession3Test() {
        emulator = AndroidEmulatorBuilder.for32Bit().build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/lession3/boss/boss_last.apk"));
        vm.setVerbose(true);
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("yzwg", true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
    }


    public static void main(String[] args) {
        Lession3Test lession2 = new Lession3Test();
    }

    @Override
    public DvmObject<?> getStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "com/twl/signer/YZWG->gContext:Landroid/content/Context;": {
                return vm.resolveClass("android/content/Context").newObject(null);
            }
        }
        return super.getStaticObjectField(vm, dvmClass, signature);
    }

    @Override
    public DvmObject<?> callObjectMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature) {
            case "android/content/pm/PackageManager->getPackagesForUid(I)[Ljava/lang/String;": {
                return new ArrayObject(new StringObject(vm, vm.getPackageName()));
            }
        }
        return super.callObjectMethod(vm, dvmObject, signature, varArg);
    }

    @Override
    public int callIntMethod(BaseVM vm, DvmObject<?> dvmObject, String signature, VarArg varArg) {
        switch (signature){
            case "java/lang/String->hashCode()I":{
                String s = dvmObject.getValue().toString();
                System.out.println(s.hashCode());
                return s.hashCode();
            }
        }
        return super.callIntMethod(vm, dvmObject, signature, varArg);
    }
}
