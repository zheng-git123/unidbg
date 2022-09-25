package com.jniunidbg;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ByteArray;
import com.github.unidbg.linux.android.dvm.array.FloatArray;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Lession2Test extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;
    private final DvmClass NativeClass;


    public Lession2Test() {
        emulator = AndroidEmulatorBuilder.for32Bit().build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/lession2/app-debug.apk"));
        vm.setVerbose(true);
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("dogpro", true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        NativeClass = vm.resolveClass("com/example/dogpro/MainActivity");
    }

    public void detectFile() {
        // public native String getdog(Dog dog);
        NativeClass.newObject(null).callJniMethod(emulator, "detectFile()V");
    }

    public String getHash() {
        DvmObject<?> dvmObject = NativeClass.newObject(null).callJniMethodObject(emulator, "getHash(Ljava.lang.String;)Ljava.lang.String;", "unidbg-android/src/test/resources/lession2/app-debug.apk");
        return dvmObject.getValue().toString();
    }

    public static void main(String[] args) {
        Lession2Test lession2 = new Lession2Test();
        String hash = lession2.getHash();
        System.out.println(hash);
    }

    @Override
    public DvmObject<?> newObjectV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "java/util/zip/ZipFile-><init>(Ljava/lang/String;)V": {
                String name = vaList.getObjectArg(0).getValue().toString();
                ZipFile zipFile = null;
                try {
                    zipFile = new ZipFile(name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return vm.resolveClass("java/util/zip/ZipFile").newObject(zipFile);
            }
        }
        return super.newObjectV(vm, dvmClass, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/util/zip/ZipFile->entries()Ljava/util/Enumeration;": {
                ZipFile zipFile = (ZipFile) dvmObject.getValue();
                java.util.Enumeration<? extends ZipEntry> entries = zipFile.entries();
                List dvmObjectList = new ArrayList<>();
                while (entries.hasMoreElements()) {
                    dvmObjectList.add(vm.resolveClass("java/util/zip/ZipEntry").newObject(entries.nextElement()));
                }
                return new Enumeration(vm, dvmObjectList);
            }
            case "java/util/zip/ZipEntry->getName()Ljava/lang/String;": {
                ZipEntry zipFile = (ZipEntry) dvmObject.getValue();
                return new StringObject(vm, zipFile.getName());
            }
            case "java/lang/String->toLowerCase()Ljava/lang/String;": {
                String str = (String) dvmObject.getValue();
                return new StringObject(vm, str.toLowerCase());
            }
            case "java/util/zip/ZipFile->getInputStream(Ljava/util/zip/ZipEntry;)Ljava/io/InputStream;": {
                ZipFile zipFile = (ZipFile) dvmObject.getValue();
                ZipEntry zipEntry = (ZipEntry) vaList.getObjectArg(0).getValue();
                try {
                    InputStream inputStream = zipFile.getInputStream(zipEntry);
                    return vm.resolveClass("java/io/InputStream").newObject(inputStream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            case "java/security/MessageDigest->digest()[B": {
                MessageDigest messageDigest = (MessageDigest) dvmObject.getValue();
                byte[] digest = messageDigest.digest();
                return new ByteArray(vm, digest);
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public boolean callBooleanMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/String->endsWith(Ljava/lang/String;)Z": {
                String str = dvmObject.getValue().toString();
                String param = vaList.getObjectArg(0).getValue().toString();
                return str.endsWith(param);
            }
        }
        return super.callBooleanMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public int callIntMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/io/InputStream->read([B)I": {
                InputStream inputStream = (InputStream) dvmObject.getValue();
                byte[] param = (byte[]) vaList.getObjectArg(0).getValue();
                try {
                    return inputStream.read(param);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/security/MessageDigest->update([B)V": {
                MessageDigest messageDigest = (MessageDigest) dvmObject.getValue();
                byte[] param = (byte[]) vaList.getObjectArg(0).getValue();
                messageDigest.update(param);
                return;
            }
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }
}
