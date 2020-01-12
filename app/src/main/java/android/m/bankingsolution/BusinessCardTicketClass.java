package android.m.bankingsolution;

public class BusinessCardTicketClass {

    public String NName, EEmail, PPhone;

    public BusinessCardTicketClass(){}

    public BusinessCardTicketClass(String bcname,String bcemail, String bcphone){
        this.NName = bcname;
        this.EEmail = bcemail;
        this.PPhone = bcphone;
    }

    public String getName(){return NName;}
    public String getEmail(){return EEmail;}
    public String getPhone(){return PPhone;}

    public void setName(String namee) {this.NName = namee;}
    public void setEmail(String emaill) {this.EEmail = emaill;}
    public void setPhone(String phonee) {this.PPhone = phonee;}
}
