package com.gkev.InvoicingSystem.Utils;

import com.gkev.InvoicingSystem.models.Enums.Channel;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class ChannelManager<T, D> {

    private final Map<Channel, T> services;

    protected ChannelManager(
            List<T> services,
            Function<T, Channel> channelExtractor) {

        this.services = services.stream()
                .collect(Collectors.toMap(
                        channelExtractor,
                        Function.identity()));
    }

    protected T get(Channel channel) {
        return Optional.ofNullable(services.get(channel))
                .orElseThrow(() ->
                        new IllegalArgumentException("Unsupported channel: " + channel));
    }
}