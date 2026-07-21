package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Utils.ChannelManager;
import com.gkev.InvoicingSystem.models.DTO.OverdueInvoiceDTO;
import com.gkev.InvoicingSystem.models.Enums.Channel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
@Service
public class OverdueInvoiceNotification extends ChannelManager<DailyOverdueChannel, OverdueInvoiceDTO> {
    protected OverdueInvoiceNotification(
                                         List<DailyOverdueChannel> services) {
        super(services, DailyOverdueChannel::channel);
    }
    public Mono<Void> send(Channel channel, OverdueInvoiceDTO dto) {
        return get(channel).send(dto);
    }
}
