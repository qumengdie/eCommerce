package com.qumengdie.ecommerce.service;

import com.qumengdie.ecommerce.payload.StripePaymentDTO;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

public interface StripeService {
  PaymentIntent paymentIntent(StripePaymentDTO stripePaymentDTO) throws StripeException;
}
