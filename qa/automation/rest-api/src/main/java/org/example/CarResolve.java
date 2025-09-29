
package org.example;

public class CarResolve{
    private Integer brandId;
    private Integer modelId;
    private Integer yearId;
    private Integer engineId;
    private Integer transmissionId;
    private Integer wheelDriveId;

    public CarResolve(Integer brandId, Integer modelId, Integer yearId, Integer engineId, Integer transmissionId, Integer wheelDriveId) {
        this.brandId = brandId;
        this.modelId = modelId;
        this.yearId = yearId;
        this.engineId = engineId;
        this.transmissionId = transmissionId;
        this.wheelDriveId = wheelDriveId;
    }

    public CarResolve() {
    }


    public Integer getBrandId() {
        return brandId;
    }

    public void setBrandId(Integer brandId) {
        this.brandId = brandId;
    }

    public Integer getModelId() {
        return modelId;
    }

    public void setModelId(Integer modelId) {
        this.modelId = modelId;
    }

    public Integer getYearId() {
        return yearId;
    }

    public void setYearId(Integer yearId) {
        this.yearId = yearId;
    }

    public Integer getEngineId() {
        return engineId;
    }

    public void setEngineId(Integer engineId) {
        this.engineId = engineId;
    }

    public Integer getTransmissionId() {
        return transmissionId;
    }

    public void setTransmissionId(Integer transmissionId) {
        this.transmissionId = transmissionId;
    }

    public Integer getWheelDriveId() {
        return wheelDriveId;
    }

    public void setWheelDriveId(Integer wheelDriveId) {
        this.wheelDriveId = wheelDriveId;
    }

    @Override
    public String toString() {
        return "{" +
                "brandId = " + brandId + "," +
                "modelId = " + modelId + "," +
                "yearId = " + yearId + "," +
                "engineId = " + engineId + "," +
                "transmissionId = " + transmissionId + "," +
                "wheelDriveId = " + wheelDriveId +
                '}';
    }
}
