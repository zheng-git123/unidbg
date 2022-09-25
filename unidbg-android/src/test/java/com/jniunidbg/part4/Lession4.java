package com.jniunidbg.part4;

import com.github.unidbg.AndroidEmulator;
import com.github.unidbg.Module;
import com.github.unidbg.linux.android.AndroidEmulatorBuilder;
import com.github.unidbg.linux.android.AndroidResolver;
import com.github.unidbg.linux.android.dvm.*;
import com.github.unidbg.linux.android.dvm.array.ArrayObject;
import com.github.unidbg.linux.android.dvm.array.FloatArray;
import com.github.unidbg.memory.Memory;
import com.jniunidbg.Dog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 无障碍服务
 */
public class Lession4 extends AbstractJni{
    private final AndroidEmulator emulator;
    private final Module module;
    private final VM vm;
    private final DvmClass NativeClass;


    public Lession4() {
        emulator = AndroidEmulatorBuilder.for32Bit().build(); // 创建模拟器实例
        final Memory memory = emulator.getMemory();
        memory.setLibraryResolver(new AndroidResolver(23)); // 设置系统类库解析
        vm = emulator.createDalvikVM(new File("unidbg-android/src/test/resources/lession4/app-debug.apk"));
        vm.setVerbose(true);
        vm.setJni(this);
        DalvikModule dm = vm.loadLibrary("dogplus", true);
        dm.callJNI_OnLoad(emulator);
        module = dm.getModule();
        NativeClass = vm.resolveClass("com/example/dogplus/MainActivity");
    }

    public void calldetectAccessibilityManager(){
        NativeClass.newObject(null).callJniMethod(emulator, "detectAccessibilityManager()V");
    };


    public static void main(String[] args) {
        Lession4 lession = new Lession4();
        lession.calldetectAccessibilityManager();
    }

    @Override
    public DvmObject<?> callObjectMethodV(BaseVM vm, DvmObject<?> dvmObject, String signature, VaList vaList) {
        switch (signature){
            case "android/app/ActivityThread->getApplication()Landroid/app/Application;":
                return vm.resolveClass("android/app/Application").newObject(null);
            case "android/view/accessibility/AccessibilityManager->getInstalledAccessibilityServiceList()Ljava/util/List;":
                List<DvmObject<?>> AccessibilityServiceList = new ArrayList<>();
                AccessibilityServiceList.add(vm.resolveClass("android/accessibilityservice/AccessibilityServiceInfo").newObject(null));
                AccessibilityServiceList.add(vm.resolveClass("android/accessibilityservice/AccessibilityServiceInfo").newObject(null));
                AccessibilityServiceList.add(vm.resolveClass("android/accessibilityservice/AccessibilityServiceInfo").newObject(null));
                return new ArrayListObject(vm,AccessibilityServiceList);
            case "android/accessibilityservice/AccessibilityServiceInfo->getResolveInfo()Landroid/content/pm/ResolveInfo;":
                return vm.resolveClass("android/content/pm/ResolveInfo").newObject(null);
            case "android/content/pm/ServiceInfo->loadLabel(Landroid/content/pm/PackageManager;)Ljava/lang/CharSequence;":
                return vm.resolveClass("java/lang/CharSequence").newObject(null);
        }
        return super.callObjectMethodV(vm, dvmObject, signature, vaList);
    }

    @Override
    public DvmObject<?> getObjectField(BaseVM vm, DvmObject<?> dvmObject, String signature) {
        switch (signature){
            case "android/content/pm/ResolveInfo->serviceInfo:Landroid/content/pm/ServiceInfo;":
                return vm.resolveClass("android/content/pm/ServiceInfo").newObject(null);
            case "android/content/pm/ServiceInfo->name:Ljava/lang/String;":
                return new StringObject(vm,"Accessibi");
            case "android/content/pm/ServiceInfo->packageName:Ljava/lang/String;":
                return new StringObject(vm,"com.android.settings");

        }
        return super.getObjectField(vm, dvmObject, signature);
    }
}
