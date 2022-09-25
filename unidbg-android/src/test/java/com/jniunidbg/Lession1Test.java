package com.jniunidbg;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.FloatArray;
import com.github.unidbg.memory.Memory;

import java.io.File;
import java.util.UUID;

public class Lession1Test extends AbstractJni {
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;
    private final DvmClass NativeClass;


    public Lession1Test() {
        emulator = AndroidEmulatorBuilder.for32Bit().build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/lession2/app-debug.apk"));
        vm.setVerbose(true);
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("dogapp", true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        NativeClass = vm.resolveClass("com/example/dogapp/MainActivity");
    }

    public String getDog() {
        // public native String getdog(Dog dog);
        DvmObject Dog = vm.resolveClass("com/example/dogapp/Dog").newObject(new Dog("小美", 5, 36.8f, new float[]{37.3f, 36.9f}));
        StringObject stringObject = NativeClass.newObject(null).callJniMethodObject(emulator, "getdog(Lcom/example/dogapp/Dog;)Ljava/lang/String;", Dog);
        return stringObject.getValue();
    }

    public static void main(String[] args) {
        Lession1Test lession2 = new Lession1Test();
        String ret = lession2.getDog();
        System.out.println("ret:" + ret);
    }

    @Override
    public DvmObject<?> callStaticObjectMethodV(BaseVM vm, DvmClass dvmClass, String signature, VaList vaList) {
        switch (signature) {
            case "com/example/dogapp/Dog->getAnimalType()Ljava/lang/String;":
                String animalType = Dog.getAnimalType();
                return new StringObject(vm, animalType);
            case "java/util/UUID->randomUUID()Ljava/util/UUID;":
                return vm.resolveClass("java/util/UUID").newObject(UUID.randomUUID());
        }
        return super.callStaticObjectMethodV(vm, dvmClass, signature, vaList);
    }

    @Override
    public void setStaticObjectField(BaseVM vm, DvmClass dvmClass, String signature, DvmObject<?> value) {
        switch (signature) {
            case "com/example/dogapp/Dog->type:Ljava/lang/String;":
                Dog.type = value.getValue().toString();
                return;
        }
        super.setStaticObjectField(vm, dvmClass, signature, value);
    }

    @Override
    public int callIntMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "com/example/dogapp/Dog->getAge()I":
                Dog dog = (Dog) dvmObject.getValue();
                return dog.getAge();
        }
        return super.callIntMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public float callFloatMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "com/example/dogapp/Dog->getTemperature()F":
                Dog dog = (Dog) dvmObject.getValue();
                return dog.getTemperature();
        }
        return super.callFloatMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "com/example/dogapp/Dog->getName()Ljava/lang/String;":
                Dog dog1 = (Dog) dvmObject.getValue();
                return new StringObject(vm, dog1.getName());
            case "com/example/dogapp/Dog->getTemperatureHistory()[F":
                Dog dog2 = (Dog) dvmObject.getValue();
                return new FloatArray(vm, dog2.getTemperatureHistory());
            case "java/util/UUID->toString()Ljava/lang/String;":
                UUID uuid = (UUID) dvmObject.getValue();
                return new StringObject(vm, uuid.toString());
            case "java/lang/StringBuilder->append(Ljava/lang/String;)Ljava/lang/StringBuilder;": {
                StringBuilder stringBuilder = (StringBuilder) dvmObject.getValue();
                String s = vaList.getObjectArg(0).getValue().toString();
                stringBuilder.append(s);
                return vm.resolveClass("java/lang/StringBuilder").newObject(stringBuilder);
            }
            case "java/lang/StringBuilder->append(I)Ljava/lang/StringBuilder;": {
                StringBuilder stringBuilder = (StringBuilder) dvmObject.getValue();
                int s = vaList.getIntArg(0);
                stringBuilder.append(s);
                return vm.resolveClass("java/lang/StringBuilder").newObject(stringBuilder);
            }
            case "java/lang/StringBuilder->append(F)Ljava/lang/StringBuilder;": {
                StringBuilder stringBuilder = (StringBuilder) dvmObject.getValue();
                float s = vaList.getFloatArg(0);
                stringBuilder.append(s);
                return vm.resolveClass("java/lang/StringBuilder").newObject(stringBuilder);
            }
            case "java/lang/StringBuilder->toString()Ljava/lang/String;": {
                StringBuilder stringBuilder = (StringBuilder) dvmObject.getValue();
                return new StringObject(vm,stringBuilder.toString());
            }
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> allocObject(BaseVM vm, DvmClass dvmClass, String signature) {
        switch (signature) {
            case "java/lang/StringBuilder->allocObject": {
                //  写法1
//                return vm.resolveClass("java/lang/StringBuilder").newObject(new StringBuilder());
                // 写法2
                return dvmClass.newObject(new StringBuilder());
            }
        }
        return super.allocObject(vm, dvmClass, signature);
    }

    @Override
    public void callVoidMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature) {
            case "java/lang/StringBuilder-><init>()V": {
                return;
            }
        }
        super.callVoidMethodV(vm, dvmObject, signature, vaList);
    }
}
