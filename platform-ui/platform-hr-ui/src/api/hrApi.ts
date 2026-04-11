import axios from "axios";

const BASE = "http://localhost:8085/api";

const getToken = () => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.token || null;
  } catch { return null; }
};

const api = axios.create({ baseURL: BASE, headers: { "Content-Type": "application/json" } });
api.interceptors.request.use((config) => {
  const token = getToken();
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// ─── Tipler ────────────────────────────────────────────────────────────────

export interface Department {
  id: string; companyId: string; name: string; code: string;
  description?: string; parentDepartmentId?: string; active: boolean;
}
export interface Position {
  id: string; companyId: string; title: string; code: string;
  description?: string; departmentId?: string; level?: string; active: boolean;
}
export interface Employee {
  id: string; companyId: string; departmentId?: string; positionId?: string;
  employeeNumber?: string; firstName: string; lastName: string; fullName: string;
  phone?: string; workEmail?: string; city?: string;
  employmentType?: string; employmentTypeLabel?: string;
  status?: string; statusLabel?: string;
  annualLeaveDays?: number; usedLeaveDays?: number; remainingLeaveDays?: number;
}

// ─── API'lar ────────────────────────────────────────────────────────────────

export const departmentApi = {
  getByCompany: (companyId: string) => api.get<any>(`/departments/company/${companyId}`),
  create:  (data: any) => api.post<any>("/departments", data),
  update:  (id: string, data: any) => api.put<any>(`/departments/${id}`, data),
  delete:  (id: string) => api.delete<any>(`/departments/${id}`),
};

export const positionApi = {
  getByCompany: (companyId: string) => api.get<any>(`/positions/company/${companyId}`),
  create: (data: any) => api.post<any>("/positions", data),
  update: (id: string, data: any) => api.put<any>(`/positions/${id}`, data),
  delete: (id: string) => api.delete<any>(`/positions/${id}`),
};

export const employeeApi = {
  getByCompany:  (companyId: string) => api.get<any>(`/employees/company/${companyId}`),
  getByDept:     (deptId: string)    => api.get<any>(`/employees/department/${deptId}`),
  getById:       (id: string)        => api.get<any>(`/employees/${id}`),
  search:        (companyId: string, q: string) => api.get<any>(`/employees/search?companyId=${companyId}&q=${q}`),
  create:        (data: any)         => api.post<any>("/employees", data),
  update:        (id: string, data: any) => api.put<any>(`/employees/${id}`, data),
  updateStatus:  (id: string, status: string) => api.patch<any>(`/employees/${id}/status?status=${status}`),
};
