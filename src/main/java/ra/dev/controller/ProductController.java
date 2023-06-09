package ra.dev.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ra.dev.dto.respone.GetProduct;
import ra.dev.model.service.ProductService;

import java.util.List;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/product")
public class ProductController {
    @Autowired
    ProductService productService;
    @GetMapping()
    public List<GetProduct> productList(){
        return productService.getAll();
    }

}
