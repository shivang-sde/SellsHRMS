package com.sellspark.SellsHRMS.controller.test;



import com.sellspark.SellsHRMS.scheduler.PayrollAutoScheduler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test/payroll")
@RequiredArgsConstructor
public class PayrollTestController {

    private final PayrollAutoScheduler scheduler;

    @PostMapping("/run")
    public String runAutoPayroll() {
        scheduler.autoGeneratePayslips();
        return "✅ Auto payroll scheduler triggered manually!";
    }
}