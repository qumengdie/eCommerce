import api from '../../api/api';

export const fetchProducts = (queryString) => async (dispatch) => {
  try {
    dispatch({ type: 'IS_FETCHING' });

    const { data } = await api.get(`/public/products?${queryString}`);
    dispatch({
      type: 'FETCH_PRODUCTS',
      payload: data.content,
      pageNumber: data.pageNumber,
      pageSize: data.pageSize,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      lastPage: data.lastPage,
    });

    dispatch({ type: 'IS_SUCCESS' });
  } catch (error) {
    console.error('Error fetching products:', error);
    dispatch({
      type: 'IS_ERROR',
      payload: error?.response?.data?.message || 'Failed to fetch products',
    });
  }
};

export const fetchCategories = () => async (dispatch) => {
  try {
    dispatch({ type: 'CATEGORY_LOADER' });

    const { data } = await api.get(`/public/categories`);
    dispatch({
      type: 'FETCH_CATEGORIES',
      payload: data.content,
      pageNumber: data.pageNumber,
      pageSize: data.pageSize,
      totalElements: data.totalElements,
      totalPages: data.totalPages,
      lastPage: data.lastPage,
    });

    dispatch({ type: 'CATEGORY_SUCCESS' });
  } catch (error) {
    console.error('Error fetching products:', error);
    dispatch({
      type: 'IS_ERROR',
      payload: error?.response?.data?.message || 'Failed to fetch categories',
    });
  }
};

export const addToCart =
  (data, qty = 1, toast) =>
  (dispatch, getState) => {
    // find the product
    const { products } = getState().products;
    const getProduct = products.find(
      (item) => item.productId === data.productId
    );

    // check for stocks
    const isQtyExist = getProduct.quantity >= qty;

    // if in stock -> add, else -> error
    if (isQtyExist) {
      dispatch({ type: 'ADD_CART', payload: { ...data, quantity: qty } });
      localStorage.setItem('cartItems', JSON.stringify(getState().carts.cart));
      toast.success(`${data?.productName} added to the cart`);
    } else {
      toast.error('Out of Stock');
    }
  };

export const increaseCartQuantity =
  (data, toast, currentQuantity, setCurrentQuantity) =>
  (dispatch, getState) => {
    const { products } = getState().products;
    const getProduct = products.find(
      (item) => item.productId === data.productId
    );

    const isQtyExist = getProduct.quantity >= currentQuantity + 1;

    if (isQtyExist) {
      const newQuantity = currentQuantity + 1;
      setCurrentQuantity(newQuantity);

      dispatch({
        type: 'ADD_CART',
        payload: { ...data, quantity: newQuantity },
      });

      localStorage.setItem('cartItems', JSON.stringify(getState().carts.cart));
    } else {
      toast.error('Quantity Reached to Limit');
    }
  };

export const decreaseCartQuantity =
  (data, newQuantity) => (dispatch, getState) => {
    dispatch({
      type: 'ADD_CART',
      payload: { ...data, quantity: newQuantity },
    });
    localStorage.setItem('cartItems', JSON.stringify(getState().carts.cart));
  };

export const removeFromCart = (data, toast) => (dispatch, getState) => {
  dispatch({ type: 'REMOVE_CART', payload: data });
  toast.success(`${data.productName} removed from cart`);
  localStorage.setItem('cartItems', JSON.stringify(getState().carts.cart));
};

export const authenticateSingInUser =
  (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
    try {
      setLoader(true);
      const { data } = await api.post('/auth/signin', sendData);
      dispatch({ type: 'LOGIN_USER', payload: data });
      localStorage.setItem('auth', JSON.stringify(data));
      reset();
      toast.success('Login Success');
      navigate('/');
    } catch (error) {
      console.log(error);
      toast.error(error?.response?.data?.message || 'Internal Server Error');
    } finally {
      setLoader(false);
    }
  };

export const registerNewUser =
  (sendData, toast, reset, navigate, setLoader) => async (dispatch) => {
    try {
      setLoader(true);
      const { data } = await api.post('/auth/signup', sendData);
      reset();
      toast.success(data?.message || 'User Registered Successfully');
      navigate('/login');
    } catch (error) {
      console.log(error);
      toast.error(
        error?.response?.data?.message ||
          error?.response?.data?.password ||
          'Internal Server Error'
      );
    } finally {
      setLoader(false);
    }
  };

export const logOutUser = (navigate) => (dispatch) => {
  dispatch({ type: 'LOG_OUT' });
  localStorage.removeItem('auth');
  navigate('/login');
};

export const addUpdateUserAddress =
  (sendData, toast, addressId, setOpenAddressModal) =>
  async (dispatch, getState) => {
    dispatch({ type: 'BUTTON_LOADER' });
    try {
      if (addressId) {
        // if addressId exists, need to update the address
        await api.put(`/addresses/${addressId}`, sendData);
      } else {
        // if no addressId, need to add a new address
        const { data } = await api.post('/addresses', sendData);
      }
      dispatch(getUserAddresses());

      toast.success('Address saved successfully');
      dispatch({ type: 'IS_SUCCESS' });
    } catch (error) {
      console.log(error);
      toast.error(error?.response?.data?.message || 'Internal Server Error');
      dispatch({ type: 'IS_ERROR', payload: null });
    } finally {
      setOpenAddressModal(false);
    }
  };

export const getUserAddresses = () => async (dispatch, getState) => {
  try {
    dispatch({ type: 'IS_FETCHING' });
    const { data } = await api.get(`/addresses`);
    dispatch({ type: 'USER_ADDRESS', payload: data });
    dispatch({ type: 'IS_SUCCESS' });
  } catch (error) {
    console.log(error);
    dispatch({
      type: 'IS_ERROR',
      payload:
        error?.response?.data?.message || 'Failed to fetch user addresses',
    });
  }
};

export const selectUserCheckoutAddress = (address) => {
  return {
    type: 'SELECT_CHECKOUT_ADDRESS',
    payload: address,
  };
};
