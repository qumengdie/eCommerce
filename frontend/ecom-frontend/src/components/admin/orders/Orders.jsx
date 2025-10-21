import React from 'react';
import { FaShoppingCart } from 'react-icons/fa';
import OrderTable from './OrderTable';

const Orders = () => {
  const adminOrders = [
    {
      orderId: 1,
      email: 'user1@example.com',
      orderItems: [
        {
          orderItemId: 1,
          product: {
            productId: 1,
            productName:
              'Adjustable dumbbell set for home workouts | Premium Quality',
            image: '9e663e18-0db7-4cd6-ba39-c93a392c1b04.png',
            description:
              'Adjustable dumbbell set for home workouts, can be used indoors, outdoors, at your personal gym. This is available at lowest possible rates.',
            quantity: 84,
            price: 90.0,
            discount: 10.0,
            specialPrice: 81.0,
          },
          quantity: 2,
          discount: 10.0,
          orderedProductPrice: 81.0,
        },
      ],
      orderDate: '2025-10-02',
      payment: {
        paymentId: 1,
        paymentMethod: 'CARD',
        pgPaymentId: 'pi_1FHEhK2eZvKYlo2CcK4UJNdW',
        pgStatus: 'succeeded',
        pgResponseMessage: 'Payment successful',
        pgName: 'Stripe',
      },
      totalAmount: 162.0,
      orderStatus: 'Order Accepted!',
      addressId: 2,
    },
    {
      orderId: 2,
      email: 'user1@example.com',
      orderItems: [
        {
          orderItemId: 2,
          product: {
            productId: 1,
            productName:
              'Adjustable dumbbell set for home workouts | Premium Quality',
            image: '9e663e18-0db7-4cd6-ba39-c93a392c1b04.png',
            description:
              'Adjustable dumbbell set for home workouts, can be used indoors, outdoors, at your personal gym. This is available at lowest possible rates.',
            quantity: 84,
            price: 90.0,
            discount: 10.0,
            specialPrice: 81.0,
          },
          quantity: 3,
          discount: 10.0,
          orderedProductPrice: 81.0,
        },
      ],
      orderDate: '2025-10-02',
      payment: {
        paymentId: 2,
        paymentMethod: 'CARD',
        pgPaymentId: 'pi_1FHEhK2eZvKYlo2CcK4UJNdW',
        pgStatus: 'succeeded',
        pgResponseMessage: 'Payment successful',
        pgName: 'Stripe',
      },
      totalAmount: 243.0,
      orderStatus: 'Order Accepted!',
      addressId: 2,
    },
  ];

  const pagination = {
    pageNumber: 0,
    pageSize: 50,
    totalElements: 11,
    totalPages: 1,
    lastPage: true,
  };

  const emptyOrder = !adminOrders || adminOrders?.length === 0;

  return (
    <div className="pb-6 pt-20">
      {emptyOrder ? (
        <div className="flex flex-col items-center justify-center text-gray-600 py-10">
          <FaShoppingCart size={50} className="mb-3" />
          <h2 className="text-2xl font-semibold">No Orders Placed Yet</h2>
        </div>
      ) : (
        <div>
          <OrderTable adminOrder={adminOrders} pagination={pagination} />
        </div>
      )}
    </div>
  );
};

export default Orders;
