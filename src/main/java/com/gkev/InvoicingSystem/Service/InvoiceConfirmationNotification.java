package com.gkev.InvoicingSystem.Service;

import com.gkev.InvoicingSystem.Utils.ChannelManager;
import com.gkev.InvoicingSystem.models.DTO.InvoiceConfirmationDTO;
import com.gkev.InvoicingSystem.models.DTO.InvoiceConfirmationResDTO;
import com.gkev.InvoicingSystem.models.Enums.Channel;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
public class InvoiceConfirmationNotification  extends ChannelManager<InvoiceConfirmationChannel, InvoiceConfirmationDTO> {


    protected InvoiceConfirmationNotification(
            List<InvoiceConfirmationChannel> services) {
        super(services, InvoiceConfirmationChannel::channel);
    }
    public Mono<Void> send(Channel channel, InvoiceConfirmationResDTO dto) {
        return get(channel).send(dto);
    }
}
