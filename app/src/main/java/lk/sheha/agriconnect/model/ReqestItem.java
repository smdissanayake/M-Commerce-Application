package lk.sheha.agriconnect.model;

import java.io.Serializable;

public class ReqestItem implements Serializable {
    private String fromuser,hireowner,requestId,paymentStatus,amount,seen,product,time,totqty;

    public ReqestItem(String fromuser, String hireowner, String requestId, String paymentStatus, String amount, String seen, String product, String time,String totqty) {
        this.fromuser = fromuser;
        this.hireowner = hireowner;
        this.requestId = requestId;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.seen = seen;
        this.product = product;
        this.time = time;
        this.totqty =totqty;
    }

    public String getTotqty() {
        return totqty;
    }

    public void setTotqty(String totqty) {
        this.totqty = totqty;
    }

    public String getFromuser() {
        return fromuser;
    }

    public void setFromuser(String fromuser) {
        this.fromuser = fromuser;
    }

    public String getHireowner() {
        return hireowner;
    }

    public void setHireowner(String hireowner) {
        this.hireowner = hireowner;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSeen() {
        return seen;
    }

    public void setSeen(String seen) {
        this.seen = seen;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
