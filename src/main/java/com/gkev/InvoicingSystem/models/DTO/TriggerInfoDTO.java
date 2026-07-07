package com.gkev.InvoicingSystem.models.DTO;

import java.io.Serializable;

public record TriggerInfoDTO(int triggerCount,
                             boolean isRunForever,
                             long timeInterval,
                             long initialOffset,
                             String info
) implements Serializable { }