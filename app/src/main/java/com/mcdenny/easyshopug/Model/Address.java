package com.mcdenny.easyshopug.Model;

public class Address {

    private String AddressName;
    private String AddressArea;
    private String AddressDistrict;
    private String AddressCounty;
    private String AddressPhone;
    private String AddressDivision;

    public Address() {

    }

    public Address(String name, String area, String district, String county, String phone, String division) {
        AddressName = name;
        AddressPhone = phone;
        AddressDistrict = district;
        AddressCounty = county;
        AddressDivision = division;
        AddressArea = area;
    }

    public String getAddressName() {
        return AddressName;
    }

    public void setAddressName(String name) {
        name = name;
    }
    public String getAddressDistrict() {
        return AddressDistrict;
    }
    public void setAddressDistrict(String district) {
        district = district;
    }

    public String getAddressCounty() {
        return AddressCounty;
    }

    public void setAddressCounty(String county) {
        county = county;
    }

    public String getAddressDivision() {
        return AddressDivision;
    }
    public void setAddressDivision(String division) {
        division = division;
    }

    public String getAddressArea() {
        return AddressArea;
    }
    public void setAddressArea(String area) {
        area = area;
    }

    public String getAddressPhone() {
        return AddressPhone;
    }

    public void setAddressPhone(String phone) {
        AddressPhone = phone;
    }
}
