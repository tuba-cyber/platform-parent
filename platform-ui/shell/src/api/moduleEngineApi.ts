/**
 * moduleEngineApi.ts
 *
 * platform-module-engine servisi (port 8081) ile iletişim kuran API katmanı.
 * Modül → Bölüm (Section) → Ekran (Screen) hiyerarşisini yönetir.
 *
 * Hiyerarşi:
 *   Module  (kod, ad, ikon, sıra, aktif/pasif)
 *     └── Section  (bölüm — örn. "Yönetim", "Raporlar")
 *           └── Screen   (ekran — JSON şablonu içerir)
 */

import axios from "axios";

const BASE = "http://localhost:8081/api/v1";

const getToken = () => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.token || null;
  } catch { return null; }
};

const api = axios.create({ baseURL: BASE, headers: { "Content-Type": "application/json" } });
api.interceptors.request.use((cfg) => {
  const t = getToken();
  if (t) cfg.headers.Authorization = `Bearer ${t}`;
  return cfg;
});

// ─── Tipler ────────────────────────────────────────────────────────────────

export type ScreenType = "LIST" | "FORM" | "DETAIL" | "MAP" | "DASHBOARD" | "CUSTOM";

export interface PlatformModule {
  id: string;
  code: string;
  name: string;
  description?: string;
  icon?: string;
  color?: string;
  orderIndex: number;
  visible: boolean;
  active: boolean;
  companyId?: string;
}

export interface Section {
  id: string;
  name: string;
  description?: string;
  icon?: string;
  orderIndex: number;
  visible: boolean;
  active: boolean;
}

export interface Screen {
  id: string;
  name: string;
  code: string;
  description?: string;
  icon?: string;
  orderIndex: number;
  visible: boolean;
  active: boolean;
  screenType: ScreenType;
  template?: string;        // JSON string olarak saklanır
  requiredPermission?: string;
}

// ─── Modül API ──────────────────────────────────────────────────────────────

export const moduleApi = {
  getAll:      ()                            => api.get<any>("/modules"),
  getById:     (id: string)                  => api.get<any>(`/modules/${id}`),
  create:      (data: Partial<PlatformModule>) => api.post<any>("/modules", data),
  update:      (id: string, data: Partial<PlatformModule>) => api.put<any>(`/modules/${id}`, data),
  toggle:      (id: string)                  => api.patch<any>(`/modules/${id}/toggle`),
  delete:      (id: string)                  => api.delete<any>(`/modules/${id}`),
};

// ─── Bölüm (Section) API ───────────────────────────────────────────────────

export const sectionApi = {
  getByModule: (moduleId: string)            => api.get<any>(`/modules/${moduleId}/sections`),
  getById:     (id: string)                  => api.get<any>(`/sections/${id}`),
  create:      (moduleId: string, data: any) => api.post<any>(`/modules/${moduleId}/sections`, data),
  update:      (id: string, data: any)       => api.put<any>(`/sections/${id}`, data),
  delete:      (id: string)                  => api.delete<any>(`/sections/${id}`),
};

// ─── Ekran (Screen) API ────────────────────────────────────────────────────

export const screenApi = {
  getBySection:    (sectionId: string)              => api.get<any>(`/sections/${sectionId}/screens`),
  getById:         (id: string)                     => api.get<any>(`/screens/${id}`),
  getByCode:       (code: string)                   => api.get<any>(`/screens/code/${code}`),
  create:          (sectionId: string, data: any)   => api.post<any>(`/sections/${sectionId}/screens`, data),
  update:          (id: string, data: any)          => api.put<any>(`/screens/${id}`, data),
  updateTemplate:  (id: string, template: string)   => api.patch<any>(`/screens/${id}/template`, template, {
    headers: { "Content-Type": "text/plain" },
  }),
  delete:          (id: string)                     => api.delete<any>(`/screens/${id}`),
};

// ─── Widget Şablonu Tipleri ────────────────────────────────────────────────
// Bu tipler JSON template içindeki widget'ları tanımlar.
// Screen.template alanına JSON.stringify ile kaydedilir.

export type WidgetType =
  | "HEADING"        // Başlık (h1-h3)
  | "PARAGRAPH"      // Metin paragrafı
  | "DIVIDER"        // Yatay ayırıcı çizgi
  | "STATS_ROW"      // İstatistik kart satırı
  | "TABLE"          // Veri tablosu
  | "FORM_SECTION"   // Form bölümü (alanlar)
  | "BUTTON_ROW"     // Eylem butonları satırı
  | "ALERT";         // Bilgi/uyarı kutusu

export interface TableColumn {
  key: string;
  label: string;
  type: "text" | "badge" | "status" | "date" | "number";
}

export interface FormField {
  key: string;
  label: string;
  fieldType: "text" | "email" | "number" | "date" | "select" | "textarea" | "checkbox";
  required: boolean;
  colspan: 1 | 2;
}

export interface ScreenWidget {
  id: string;
  type: WidgetType;
  props: {
    // HEADING
    text?: string;
    level?: 1 | 2 | 3;
    // PARAGRAPH
    content?: string;
    // TABLE
    title?: string;
    columns?: TableColumn[];
    apiEndpoint?: string;
    searchable?: boolean;
    // FORM_SECTION
    sectionTitle?: string;
    fields?: FormField[];
    // STATS_ROW
    stats?: { label: string; valueKey: string; icon: string; color: string }[];
    // BUTTON_ROW
    buttons?: { label: string; variant: "primary" | "secondary" | "danger"; action: string }[];
    // ALERT
    message?: string;
    alertType?: "info" | "warning" | "success" | "error";
  };
}

export interface ScreenTemplate {
  version: "1.0";
  screenType: ScreenType;
  widgets: ScreenWidget[];
}
