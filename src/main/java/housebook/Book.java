package housebook;

import javax.persistence.*;
import org.springframework.beans.BeanUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="Book_table")
public class Book {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;
    private String status;
    private Long houseId;
    private String bookDate;
    private Double housePrice;

    @PostPersist
    public void onPostPersist(){
        System.out.println("##### onPostPersist status = " + this.getStatus());
        if (this.getStatus().equals("BOOKED")) {
            Booked booked = new Booked();
            BeanUtils.copyProperties(this, booked);
            booked.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            housebook.external.Payment payment = new housebook.external.Payment();
            // mappings goes here
            payment.setBookId(this.getId());
            payment.setHouseId(this.getHouseId());
            payment.setStatus(this.getStatus());
            BookApplication.applicationContext.getBean(housebook.external.PaymentService.class)
                .paymentRequest(payment);
        }
    }

    @PostUpdate
    public void onPostUpdate(){
        System.out.println("##### onPostUpdate status = " + this.getStatus());
        if (this.getStatus().equals("BOOK_CANCELED")) {
            BookCanceled bookCanceled = new BookCanceled();
            BeanUtils.copyProperties(this, bookCanceled);
            bookCanceled.publishAfterCommit();

            //Following code causes dependency to external APIs
            // it is NOT A GOOD PRACTICE. instead, Event-Policy mapping is recommended.

            housebook.external.Payment payment = new housebook.external.Payment();
            // mappings goes here
            payment.setBookId(this.getId());
            payment.setHouseId(this.getHouseId());
            payment.setStatus(this.getStatus());
            BookApplication.applicationContext.getBean(housebook.external.PaymentService.class)
                .paymentCancel(payment, this.getId());
        }
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public Long getHouseId() {
        return houseId;
    }

    public void setHouseId(Long houseId) {
        this.houseId = houseId;
    }
    public String getBookDate() {
        return bookDate;
    }

    public void setBookDate(String bookDate) {
        this.bookDate = bookDate;
    }
    public Double getHousePrice() {
        return housePrice;
    }

    public void setHousePrice(Double housePrice) {
        this.housePrice = housePrice;
    }




}
