package com.jniunidbg;

/* loaded from: classes3.dex */
public class Dog {
    static String type = "狗";
    int age;
    String name;
    float temperature;
    float[] temperatureHistory;

    public static String getAnimalType() {
        return type;
    }

    public Dog(String name, int age, float temperature, float[] temperatureHistory) {
        this.name = name;
        this.age = age;
        this.temperature = temperature;
        this.temperatureHistory = temperatureHistory;
    }

    public int getAge() {
        return this.age;
    }

    public float getTemperature() {
        return this.temperature;
    }

    public float[] getTemperatureHistory() {
        return this.temperatureHistory;
    }

    public String getName() {
        return this.name;
    }
}