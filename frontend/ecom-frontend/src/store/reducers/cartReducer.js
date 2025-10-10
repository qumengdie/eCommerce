const initialState = {
  cart: [],
  totalPrice: 0,
  cartId: null,
};

export const cartReducer = (state = initialState, action) => {
  switch (action.type) {
    case 'ADD_CART':
      const productToAdd = action.payload;
      const exists = state.cart.some(
        (item) => item.productId === productToAdd.productId
      );

      const newCart = exists
        ? state.cart.map((item) =>
            item.productId === productToAdd.productId ? productToAdd : item
          )
        : [...state.cart, productToAdd];

      return { ...state, cart: newCart };

    case 'REMOVE_CART':
      return {
        ...state,
        cart: state.cart.filter(
          (item) => item.productId !== action.payload.productId
        ),
      };

    default:
      return state;
  }
};
