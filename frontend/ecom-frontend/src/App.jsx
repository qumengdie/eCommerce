import './App.css';
import Home from './components/home/Home';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Products from './components/products/Products';
import Navbar from './components/shared/Navbar';
import About from './components/About';
import Contact from './components/Contact';
import Cart from './components/cart/Cart';
import { Toaster } from 'react-hot-toast';
import React from 'react';
import LogIn from './components/auth/LogIn';
import PrivateRoute from './components/PrivateRoute';
import Register from './components/auth/Register';
import Checkout from './components/checkout/Checkout';
import PaymentConfirmation from './components/checkout/PaymentConfirmation';
import AdminLayout from './components/admin/AdminLayout';

function App() {
  return (
    <React.Fragment>
      <Router>
        <Navbar />
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/products" element={<Products />} />
          <Route path="/about" element={<About />} />
          <Route path="/contact" element={<Contact />} />
          <Route path="/cart" element={<Cart />} />

          <Route path="/" element={<PrivateRoute />}>
            <Route path="/checkout" element={<Checkout />} />
            <Route path="/order-confirm" element={<PaymentConfirmation />} />
          </Route>

          <Route path="/" element={<PrivateRoute publicPage />}>
            <Route path="/login" element={<LogIn />} />
            <Route path="/register" element={<Register />} />
          </Route>

          <Route path="/" element={<PrivateRoute adminOnly />}>
            <Route path="/admin" element={<AdminLayout />} />
          </Route>
        </Routes>
      </Router>
      <Toaster position="bottom-center" />
    </React.Fragment>
  );
}

export default App;
