package ra.dev.dto.respone;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class GetProduct {
    private int productID;
    private String productName;
    private String image;
    private String title;
    private int price;
    private int discount;
}
