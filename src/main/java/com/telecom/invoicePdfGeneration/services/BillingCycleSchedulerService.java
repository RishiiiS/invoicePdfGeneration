package com.telecom.invoicePdfGeneration.services;

public interface BillingCycleSchedulerService {
    
    void processTodaysBillingCycle();
    
    void processBillingCycle(String cycleCode);
}
