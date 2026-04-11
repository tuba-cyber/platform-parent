import axios from "axios";

const BASE = "http://localhost:8084/api";

// Token shell'den alınır (localStorage paylaşımı)
const getToken = () => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.token || null;
  } catch {
    return null;
  }
};

const api = axios.create({ baseURL: BASE, headers: { "Content-Type": "application/json" } });
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

export interface Company {
  id: string;
  name: string;
  code: string;
  email?: string;
  phone?: string;
  website?: string;
  address?: string;
  city?: string;
  district?: string;
  country?: string;
  companyType?: string;
  companyTypeLabel?: string;
  sector?: string;
  taxNumber?: string;
  active: boolean;
}

export interface CompanyRequest {
  name: string;
  code: string;
  email?: string;
  phone?: string;
  website?: string;
  address?: string;
  city?: string;
  district?: string;
  postalCode?: string;
  country?: string;
  companyType?: string;
  sector?: string;
  taxNumber?: string;
  taxOffice?: string;
  foundedYear?: number;
  description?: string;
}

export const companyApi = {
  getAll:    ()           => api.get<any>("/companies"),
  getById:   (id: string) => api.get<any>(`/companies/${id}`),
  create:    (data: CompanyRequest) => api.post<any>("/companies", data),
  update:    (id: string, data: CompanyRequest) => api.put<any>(`/companies/${id}`, data),
  toggle:    (id: string) => api.patch<any>(`/companies/${id}/toggle`),
  search:    (keyword: string) => api.get<any>(`/companies/search?keyword=${keyword}`),
};
