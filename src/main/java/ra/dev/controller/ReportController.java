package ra.dev.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.dev.model.service.OrderService;

import java.time.LocalDate;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api/v1/report")
public class ReportController {
    @Autowired
    private OrderService orderService;

    @GetMapping("revenueByDate")
    public ResponseEntity<?> getRevenueByDate(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return ResponseEntity.ok(orderService.getRevenueByDate(start,end));
    }
}
