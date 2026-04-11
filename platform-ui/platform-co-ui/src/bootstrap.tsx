import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import CompanyRoutes from "./CompanyRoutes";
import "./index.css";

// Standalone çalıştırma (geliştirme)
const root = ReactDOM.createRoot(document.getElementById("root") as HTMLElement);
root.render(
  <BrowserRouter>
    <CompanyRoutes />
  </BrowserRouter>
);
