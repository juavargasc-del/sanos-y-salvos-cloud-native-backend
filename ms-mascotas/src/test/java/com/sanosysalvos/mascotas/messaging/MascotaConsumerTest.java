package com.sanosysalvos.mascotas.messaging;

import com.rabbitmq.client.Channel;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class MascotaConsumerTest {

    private static final long DELIVERY_TAG = 42L;

    @Test
    void recibirMascotaRegistrada_debeEnviarAckCuandoProcesaCorrectamente()
            throws Exception {
        Channel channel = mock(Channel.class);
        MascotaConsumer consumer = new MascotaConsumer(false);

        consumer.recibirMascotaRegistrada("15", channel, DELIVERY_TAG);

        verify(channel).basicAck(DELIVERY_TAG, false);
        verify(channel, never()).basicNack(DELIVERY_TAG, false, false);
    }

    @Test
    void recibirMascotaRegistrada_debeReintentarTresVecesYEnviarNack()
            throws Exception {
        Channel channel = mock(Channel.class);
        MascotaConsumer consumer = new MascotaConsumer(true);

        consumer.recibirMascotaRegistrada("15", channel, DELIVERY_TAG);

        verify(channel).basicNack(DELIVERY_TAG, false, false);
        verify(channel, never()).basicAck(DELIVERY_TAG, false);
    }
}
