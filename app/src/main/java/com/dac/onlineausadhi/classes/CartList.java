package com.dac.onlineausadhi.classes;

/**
 * Created by blood-mist on 6/2/16.
 */
public class CartList {
    protected int id;
    protected String medicineName;
    protected int quantity;
    protected String medicineType;
    protected String medicineId;
    protected String currentDate;

    public CartList() {

    }

    public CartList(String medicineName, int quantity, String medicineType, String medicineId, String currentDate) {
        this.id = id;
        this.medicineName = medicineName;
        this.quantity = quantity;
        this.medicineType=medicineType;
        this.medicineId=medicineId;
        this.currentDate=currentDate;
    }

    public int getId() {
        return this.id;
    }

    public String getMedicineType() {
        return this.medicineType;
    }

    public void setMedicineType(String medicineType) {
        this.medicineType = medicineType;
    }

    public String getMedicineId() {
        return this.medicineId;
    }

    public void setMedicineId(String medicineId) {
        this.medicineId = medicineId;
    }

    public String getCurrentDate() {
        return this.currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMedicineName() {
        return this.medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
