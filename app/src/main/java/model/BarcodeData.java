package model;

/**
 * Created by nehabh on 2/10/2016.
 */
public class BarcodeData {

    int id;
    String barcode ;
    String driverId;
    String modified;
    String activityTypeId;

    public BarcodeData(){

    }
    // constructor
    public BarcodeData(int id, String barcode, String driver_id,String modified, String activity_type_id){
        this.id = id;
        this.barcode = barcode;
        this.driverId = driver_id;
        this.modified = modified;
        this.activityTypeId = activity_type_id;

    }

    // constructor
    public BarcodeData(String barcode, String driver_id,String modified, String activity_type_id){
        this.barcode = barcode;
        this.driverId = driver_id;
        this.modified = modified;
        this.activityTypeId = activity_type_id;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getModified() {
        return modified;
    }

    public void setModified(String modified) {
        this.modified = modified;
    }

    public String getActivityTypeId() {
        return activityTypeId;
    }

    public void setActivityTypeId(String activityTypeId) {
        this.activityTypeId = activityTypeId;
    }
}
