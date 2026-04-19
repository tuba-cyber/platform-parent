/**
 * ScreenBuilderPage.tsx
 *
 * Sürükle-Bırak Ekran Tasarımcısı (Screen Builder)
 *
 * Mimari:
 *   Sol Panel  — Widget paleti (bileşen kategorileri)
 *   Orta Panel — Canvas (düzenlenebilir alan)
 *   Sağ Panel  — Seçili widget'ın özellik editörü
 *
 * Nasıl çalışır?
 * - HTML5 native Drag & Drop API kullanılır (harici kütüphane yok)
 * - Paletten canvas'a sürüklenince yeni widget eklenir
 * - Canvas içinde widget'lar sürüklenerek yeniden sıralanabilir
 * - Seçili widget'ın props'ları sağ panelden düzenlenir
 * - "Kaydet" butonu JSON template'i backend'e PATCH /screens/{id}/template ile gönderir
 *
 * Widget Tipleri:
 *   HEADING     — Başlık (h1-h3)
 *   PARAGRAPH   — Metin paragrafı
 *   DIVIDER     — Yatay ayırıcı çizgi
 *   STATS_ROW   — İstatistik kart satırı
 *   TABLE       — Veri tablosu
 *   FORM_SECTION — Form bölümü (alan listesi)
 *   BUTTON_ROW  — Eylem butonları satırı
 *   ALERT       — Bilgi / uyarı kutusu
 */

import React, { useEffect, useRef, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { screenApi, Screen, ScreenWidget, ScreenTemplate, WidgetType } from "../../api/moduleEngineApi";

// ─── Widget Metadata ──────────────────────────────────────────────────────

interface WidgetMeta {
  type: WidgetType;
  label: string;
  icon: string;
  description: string;
  defaultProps: ScreenWidget["props"];
}

const WIDGET_CATALOGUE: { category: string; items: WidgetMeta[] }[] = [
  {
    category: "Yapı",
    items: [
      {
        type: "HEADING", label: "Başlık", icon: "H", description: "Bölüm veya sayfa başlığı",
        defaultProps: { text: "Başlık", level: 1 },
      },
      {
        type: "PARAGRAPH", label: "Metin", icon: "¶", description: "Açıklama veya yönlendirme metni",
        defaultProps: { content: "Buraya açıklama metni girin." },
      },
      {
        type: "DIVIDER", label: "Ayırıcı", icon: "—", description: "Bölümleri ayıran yatay çizgi",
        defaultProps: {},
      },
      {
        type: "ALERT", label: "Uyarı Kutusu", icon: "!", description: "Bilgi veya uyarı mesajı",
        defaultProps: { message: "Bu bir bilgi mesajıdır.", alertType: "info" },
      },
    ],
  },
  {
    category: "Veri",
    items: [
      {
        type: "STATS_ROW", label: "İstatistik Satırı", icon: "📊", description: "Özet sayısal değer kartları",
        defaultProps: {
          stats: [
            { label: "Toplam", valueKey: "total", icon: "📋", color: "bg-blue-500" },
            { label: "Aktif",  valueKey: "active", icon: "✅", color: "bg-green-500" },
          ],
        },
      },
      {
        type: "TABLE", label: "Veri Tablosu", icon: "⊞", description: "API'dan gelen verileri tablo olarak gösterir",
        defaultProps: {
          title: "Veri Listesi",
          apiEndpoint: "",
          searchable: true,
          columns: [
            { key: "name", label: "Ad", type: "text" },
            { key: "status", label: "Durum", type: "status" },
          ],
        },
      },
      {
        type: "FORM_SECTION", label: "Form Bölümü", icon: "≡", description: "Giriş alanları içeren form bölümü",
        defaultProps: {
          sectionTitle: "Temel Bilgiler",
          fields: [
            { key: "name", label: "Ad", fieldType: "text", required: true, colspan: 1 },
            { key: "email", label: "E-posta", fieldType: "email", required: false, colspan: 1 },
          ],
        },
      },
    ],
  },
  {
    category: "Eylem",
    items: [
      {
        type: "BUTTON_ROW", label: "Buton Satırı", icon: "▶", description: "Kaydet, İptal gibi eylem butonları",
        defaultProps: {
          buttons: [
            { label: "Kaydet", variant: "primary", action: "submit" },
            { label: "İptal",  variant: "secondary", action: "cancel" },
          ],
        },
      },
    ],
  },
];

// ─── Şablon Ön Ayarları ───────────────────────────────────────────────────

const makeId = () => Math.random().toString(36).slice(2, 9);

const PRESETS: { label: string; icon: string; widgets: () => ScreenWidget[] }[] = [
  { label: "Boş", icon: "⬜", widgets: () => [] },
  {
    label: "Liste Şablonu", icon: "📋",
    widgets: () => [
      { id: makeId(), type: "HEADING",    props: { text: "Kayıt Listesi", level: 1 } },
      { id: makeId(), type: "STATS_ROW",  props: { stats: [{ label: "Toplam", valueKey: "total", icon: "📋", color: "bg-blue-500" }, { label: "Aktif", valueKey: "active", icon: "✅", color: "bg-green-500" }] } },
      { id: makeId(), type: "TABLE",      props: { title: "Kayıtlar", apiEndpoint: "", searchable: true, columns: [{ key: "name", label: "Ad", type: "text" }, { key: "status", label: "Durum", type: "status" }] } },
    ],
  },
  {
    label: "Form Şablonu", icon: "📝",
    widgets: () => [
      { id: makeId(), type: "HEADING",      props: { text: "Yeni Kayıt", level: 1 } },
      { id: makeId(), type: "FORM_SECTION", props: { sectionTitle: "Temel Bilgiler", fields: [{ key: "name", label: "Ad", fieldType: "text", required: true, colspan: 1 }, { key: "email", label: "E-posta", fieldType: "email", required: false, colspan: 1 }] } },
      { id: makeId(), type: "FORM_SECTION", props: { sectionTitle: "Ek Bilgiler", fields: [{ key: "notes", label: "Notlar", fieldType: "textarea", required: false, colspan: 2 }] } },
      { id: makeId(), type: "BUTTON_ROW",   props: { buttons: [{ label: "Kaydet", variant: "primary", action: "submit" }, { label: "İptal", variant: "secondary", action: "cancel" }] } },
    ],
  },
  {
    label: "Detay Şablonu", icon: "🔍",
    widgets: () => [
      { id: makeId(), type: "HEADING",    props: { text: "Kayıt Detayı", level: 1 } },
      { id: makeId(), type: "STATS_ROW",  props: { stats: [{ label: "Durum", valueKey: "status", icon: "📌", color: "bg-purple-500" }, { label: "Tarih", valueKey: "createdAt", icon: "📅", color: "bg-orange-500" }] } },
      { id: makeId(), type: "DIVIDER",    props: {} },
      { id: makeId(), type: "TABLE",      props: { title: "İlgili Kayıtlar", apiEndpoint: "", searchable: false, columns: [{ key: "name", label: "Ad", type: "text" }] } },
    ],
  },
];

// ─── Widget Önizleme Bileşenleri ──────────────────────────────────────────

const WidgetPreview: React.FC<{ widget: ScreenWidget; selected: boolean; onSelect: () => void }> = ({ widget, selected, onSelect }) => {
  const { type, props } = widget;

  const wrapper = (children: React.ReactNode) => (
    <div
      onClick={onSelect}
      className={`relative rounded-lg border-2 transition cursor-pointer group ${
        selected ? "border-primary-500 shadow-md" : "border-transparent hover:border-primary-200"
      }`}
    >
      {/* Seçim göstergesi */}
      {selected && (
        <div className="absolute -top-2 -right-2 w-4 h-4 bg-primary-500 rounded-full flex items-center justify-center">
          <svg className="w-2.5 h-2.5 text-white" fill="currentColor" viewBox="0 0 20 20">
            <path fillRule="evenodd" d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z" />
          </svg>
        </div>
      )}
      {children}
    </div>
  );

  if (type === "HEADING") return wrapper(
    <div className="px-4 py-3 bg-white rounded-lg">
      {props.level === 1 && <h1 className="text-2xl font-bold text-gray-900">{props.text}</h1>}
      {props.level === 2 && <h2 className="text-xl font-bold text-gray-800">{props.text}</h2>}
      {props.level === 3 && <h3 className="text-lg font-semibold text-gray-700">{props.text}</h3>}
    </div>
  );

  if (type === "PARAGRAPH") return wrapper(
    <div className="px-4 py-3 bg-white rounded-lg">
      <p className="text-sm text-gray-600">{props.content}</p>
    </div>
  );

  if (type === "DIVIDER") return wrapper(
    <div className="py-2 px-4"><hr className="border-gray-200" /></div>
  );

  if (type === "ALERT") {
    const colors: Record<string, string> = {
      info:    "bg-blue-50 border-blue-200 text-blue-800",
      warning: "bg-yellow-50 border-yellow-200 text-yellow-800",
      success: "bg-green-50 border-green-200 text-green-800",
      error:   "bg-red-50 border-red-200 text-red-800",
    };
    return wrapper(
      <div className={`px-4 py-3 rounded-lg border text-sm ${colors[props.alertType || "info"]}`}>
        {props.message}
      </div>
    );
  }

  if (type === "STATS_ROW") return wrapper(
    <div className="grid grid-cols-2 gap-3 p-2">
      {(props.stats || []).map((s, i) => (
        <div key={i} className="bg-white border border-gray-100 rounded-xl p-4 flex items-center gap-3 shadow-sm">
          <div className={`${s.color} w-10 h-10 rounded-lg flex items-center justify-center text-lg text-white`}>{s.icon}</div>
          <div>
            <p className="text-xs text-gray-500">{s.label}</p>
            <p className="text-xl font-bold text-gray-900">—</p>
          </div>
        </div>
      ))}
    </div>
  );

  if (type === "TABLE") return wrapper(
    <div className="bg-white rounded-xl border border-gray-100 overflow-hidden">
      <div className="px-4 py-3 border-b border-gray-100 bg-gray-50 flex items-center justify-between">
        <span className="text-sm font-semibold text-gray-700">{props.title || "Tablo"}</span>
        {props.searchable && (
          <div className="flex items-center gap-2 text-xs text-gray-400 bg-white border border-gray-200 rounded-lg px-3 py-1.5">
            🔍 Ara...
          </div>
        )}
      </div>
      <table className="w-full text-xs">
        <thead className="bg-gray-50">
          <tr>
            {(props.columns || []).map((c, i) => (
              <th key={i} className="px-4 py-2 text-left font-semibold text-gray-500 uppercase tracking-wide">{c.label}</th>
            ))}
          </tr>
        </thead>
        <tbody>
          <tr className="border-t border-gray-50">
            {(props.columns || []).map((c, i) => (
              <td key={i} className="px-4 py-3 text-gray-400 italic">örnek veri...</td>
            ))}
          </tr>
        </tbody>
      </table>
    </div>
  );

  if (type === "FORM_SECTION") return wrapper(
    <div className="bg-white rounded-xl border border-gray-100 p-4 shadow-sm">
      <h3 className="text-sm font-semibold text-gray-700 mb-3 pb-2 border-b border-gray-100">{props.sectionTitle}</h3>
      <div className="grid grid-cols-2 gap-3">
        {(props.fields || []).map((f, i) => (
          <div key={i} className={f.colspan === 2 ? "col-span-2" : ""}>
            <label className="block text-xs text-gray-500 mb-1">{f.label}{f.required && <span className="text-red-400 ml-0.5">*</span>}</label>
            <div className="h-8 bg-gray-100 rounded-lg border border-gray-200" />
          </div>
        ))}
      </div>
    </div>
  );

  if (type === "BUTTON_ROW") return wrapper(
    <div className="flex gap-3 px-2 py-2">
      {(props.buttons || []).map((b, i) => (
        <div key={i} className={`px-5 py-2 rounded-lg text-sm font-medium ${
          b.variant === "primary" ? "bg-primary-600 text-white" :
          b.variant === "danger"  ? "bg-red-500 text-white" :
          "bg-gray-100 text-gray-700"
        }`}>{b.label}</div>
      ))}
    </div>
  );

  return wrapper(
    <div className="px-4 py-3 bg-gray-50 rounded-lg text-xs text-gray-500 italic text-center">[{type}]</div>
  );
};

// ─── Özellik Editörü ──────────────────────────────────────────────────────

const PropertiesPanel: React.FC<{
  widget: ScreenWidget | null;
  onUpdate: (props: ScreenWidget["props"]) => void;
  onDelete: () => void;
}> = ({ widget, onUpdate, onDelete }) => {
  if (!widget) return (
    <div className="flex flex-col items-center justify-center h-full text-gray-400 text-center px-4">
      <svg className="w-10 h-10 mb-3 opacity-30" fill="none" stroke="currentColor" viewBox="0 0 24 24">
        <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M15 15l-2 5L9 9l11 4-5 2zm0 0l5 5M7.188 2.239l.777 2.897M5.136 7.965l-2.898-.777M13.95 4.05l-2.122 2.122m-5.657 5.656l-2.12 2.122" />
      </svg>
      <p className="text-sm font-medium text-gray-500">Bir widget seçin</p>
      <p className="text-xs mt-1">Özelliklerini buradan düzenleyebilirsiniz</p>
    </div>
  );

  const set = (k: string, v: any) => onUpdate({ ...widget.props, [k]: v });
  const inp = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500";
  const lbl = "block text-xs font-medium text-gray-600 mb-1";

  return (
    <div className="p-4 space-y-4 overflow-y-auto h-full">
      <div className="flex items-center justify-between">
        <span className="text-sm font-semibold text-gray-800">Özellikler</span>
        <button onClick={onDelete} className="text-xs text-red-400 hover:text-red-600 border border-red-100 hover:border-red-300 px-2 py-1 rounded">Sil</button>
      </div>

      {/* HEADING */}
      {widget.type === "HEADING" && (
        <>
          <div>
            <label className={lbl}>Başlık Metni</label>
            <input value={widget.props.text || ""} onChange={e => set("text", e.target.value)} className={inp} />
          </div>
          <div>
            <label className={lbl}>Büyüklük</label>
            <select value={widget.props.level || 1} onChange={e => set("level", Number(e.target.value))} className={inp}>
              <option value={1}>H1 — Büyük</option>
              <option value={2}>H2 — Orta</option>
              <option value={3}>H3 — Küçük</option>
            </select>
          </div>
        </>
      )}

      {/* PARAGRAPH */}
      {widget.type === "PARAGRAPH" && (
        <div>
          <label className={lbl}>Metin</label>
          <textarea value={widget.props.content || ""} onChange={e => set("content", e.target.value)} className={`${inp} h-24 resize-none`} />
        </div>
      )}

      {/* ALERT */}
      {widget.type === "ALERT" && (
        <>
          <div>
            <label className={lbl}>Mesaj</label>
            <textarea value={widget.props.message || ""} onChange={e => set("message", e.target.value)} className={`${inp} h-20 resize-none`} />
          </div>
          <div>
            <label className={lbl}>Tip</label>
            <select value={widget.props.alertType || "info"} onChange={e => set("alertType", e.target.value)} className={inp}>
              <option value="info">Bilgi (Mavi)</option>
              <option value="success">Başarı (Yeşil)</option>
              <option value="warning">Uyarı (Sarı)</option>
              <option value="error">Hata (Kırmızı)</option>
            </select>
          </div>
        </>
      )}

      {/* TABLE */}
      {widget.type === "TABLE" && (
        <>
          <div>
            <label className={lbl}>Başlık</label>
            <input value={widget.props.title || ""} onChange={e => set("title", e.target.value)} className={inp} />
          </div>
          <div>
            <label className={lbl}>API Endpoint</label>
            <input value={widget.props.apiEndpoint || ""} onChange={e => set("apiEndpoint", e.target.value)} className={inp} placeholder="/api/employees/company/{companyId}" />
          </div>
          <div>
            <label className="flex items-center gap-2 text-xs font-medium text-gray-600 cursor-pointer">
              <input type="checkbox" checked={widget.props.searchable || false} onChange={e => set("searchable", e.target.checked)} className="rounded" />
              Arama Kutusu Göster
            </label>
          </div>
          <div>
            <label className={lbl}>Sütunlar</label>
            {(widget.props.columns || []).map((col, i) => (
              <div key={i} className="flex gap-2 mb-2">
                <input
                  value={col.key}
                  onChange={e => {
                    const cols = [...(widget.props.columns || [])];
                    cols[i] = { ...cols[i], key: e.target.value };
                    set("columns", cols);
                  }}
                  className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs"
                  placeholder="key"
                />
                <input
                  value={col.label}
                  onChange={e => {
                    const cols = [...(widget.props.columns || [])];
                    cols[i] = { ...cols[i], label: e.target.value };
                    set("columns", cols);
                  }}
                  className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs"
                  placeholder="Başlık"
                />
                <button
                  onClick={() => {
                    const cols = (widget.props.columns || []).filter((_, ci) => ci !== i);
                    set("columns", cols);
                  }}
                  className="text-red-400 hover:text-red-600 text-xs px-1"
                >✕</button>
              </div>
            ))}
            <button
              onClick={() => set("columns", [...(widget.props.columns || []), { key: "", label: "", type: "text" }])}
              className="text-xs text-primary-600 hover:text-primary-800 border border-dashed border-primary-300 w-full py-1.5 rounded"
            >+ Sütun Ekle</button>
          </div>
        </>
      )}

      {/* FORM_SECTION */}
      {widget.type === "FORM_SECTION" && (
        <>
          <div>
            <label className={lbl}>Bölüm Başlığı</label>
            <input value={widget.props.sectionTitle || ""} onChange={e => set("sectionTitle", e.target.value)} className={inp} />
          </div>
          <div>
            <label className={lbl}>Alanlar</label>
            {(widget.props.fields || []).map((field, i) => (
              <div key={i} className="border border-gray-200 rounded-lg p-3 mb-2 space-y-2">
                <div className="flex gap-2">
                  <input
                    value={field.key}
                    onChange={e => { const f = [...(widget.props.fields || [])]; f[i] = { ...f[i], key: e.target.value }; set("fields", f); }}
                    className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs" placeholder="key"
                  />
                  <input
                    value={field.label}
                    onChange={e => { const f = [...(widget.props.fields || [])]; f[i] = { ...f[i], label: e.target.value }; set("fields", f); }}
                    className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs" placeholder="Etiket"
                  />
                  <button onClick={() => { const f = (widget.props.fields || []).filter((_, fi) => fi !== i); set("fields", f); }} className="text-red-400 hover:text-red-600 text-xs">✕</button>
                </div>
                <div className="flex gap-2">
                  <select
                    value={field.fieldType}
                    onChange={e => { const f = [...(widget.props.fields || [])]; f[i] = { ...f[i], fieldType: e.target.value as any }; set("fields", f); }}
                    className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs"
                  >
                    {["text","email","number","date","select","textarea","checkbox"].map(t => <option key={t} value={t}>{t}</option>)}
                  </select>
                  <select
                    value={field.colspan}
                    onChange={e => { const f = [...(widget.props.fields || [])]; f[i] = { ...f[i], colspan: Number(e.target.value) as any }; set("fields", f); }}
                    className="w-20 px-2 py-1.5 border border-gray-300 rounded text-xs"
                  >
                    <option value={1}>1 Sütun</option>
                    <option value={2}>2 Sütun</option>
                  </select>
                  <label className="flex items-center gap-1 text-xs text-gray-600 cursor-pointer">
                    <input type="checkbox" checked={field.required} onChange={e => { const f = [...(widget.props.fields || [])]; f[i] = { ...f[i], required: e.target.checked }; set("fields", f); }} className="rounded" />
                    Zorunlu
                  </label>
                </div>
              </div>
            ))}
            <button
              onClick={() => set("fields", [...(widget.props.fields || []), { key: "", label: "", fieldType: "text", required: false, colspan: 1 }])}
              className="text-xs text-primary-600 hover:text-primary-800 border border-dashed border-primary-300 w-full py-1.5 rounded"
            >+ Alan Ekle</button>
          </div>
        </>
      )}

      {/* BUTTON_ROW */}
      {widget.type === "BUTTON_ROW" && (
        <div>
          <label className={lbl}>Butonlar</label>
          {(widget.props.buttons || []).map((btn, i) => (
            <div key={i} className="flex gap-2 mb-2">
              <input
                value={btn.label}
                onChange={e => { const b = [...(widget.props.buttons || [])]; b[i] = { ...b[i], label: e.target.value }; set("buttons", b); }}
                className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs" placeholder="Etiket"
              />
              <select
                value={btn.variant}
                onChange={e => { const b = [...(widget.props.buttons || [])]; b[i] = { ...b[i], variant: e.target.value as any }; set("buttons", b); }}
                className="w-28 px-2 py-1.5 border border-gray-300 rounded text-xs"
              >
                <option value="primary">Birincil</option>
                <option value="secondary">İkincil</option>
                <option value="danger">Tehlike</option>
              </select>
              <button onClick={() => { const b = (widget.props.buttons || []).filter((_, bi) => bi !== i); set("buttons", b); }} className="text-red-400 hover:text-red-600 text-xs">✕</button>
            </div>
          ))}
          <button
            onClick={() => set("buttons", [...(widget.props.buttons || []), { label: "Buton", variant: "secondary", action: "" }])}
            className="text-xs text-primary-600 hover:text-primary-800 border border-dashed border-primary-300 w-full py-1.5 rounded"
          >+ Buton Ekle</button>
        </div>
      )}

      {/* STATS_ROW */}
      {widget.type === "STATS_ROW" && (
        <div>
          <label className={lbl}>İstatistikler</label>
          {(widget.props.stats || []).map((stat, i) => (
            <div key={i} className="border border-gray-200 rounded-lg p-3 mb-2 space-y-2">
              <div className="flex gap-2">
                <input
                  value={stat.label}
                  onChange={e => { const s = [...(widget.props.stats || [])]; s[i] = { ...s[i], label: e.target.value }; set("stats", s); }}
                  className="flex-1 px-2 py-1.5 border border-gray-300 rounded text-xs" placeholder="Etiket"
                />
                <input
                  value={stat.icon}
                  onChange={e => { const s = [...(widget.props.stats || [])]; s[i] = { ...s[i], icon: e.target.value }; set("stats", s); }}
                  className="w-14 px-2 py-1.5 border border-gray-300 rounded text-xs" placeholder="İkon"
                />
                <button onClick={() => { const s = (widget.props.stats || []).filter((_, si) => si !== i); set("stats", s); }} className="text-red-400 hover:text-red-600 text-xs">✕</button>
              </div>
              <input
                value={stat.valueKey}
                onChange={e => { const s = [...(widget.props.stats || [])]; s[i] = { ...s[i], valueKey: e.target.value }; set("stats", s); }}
                className="w-full px-2 py-1.5 border border-gray-300 rounded text-xs" placeholder="API veri anahtarı (valueKey)"
              />
            </div>
          ))}
          <button
            onClick={() => set("stats", [...(widget.props.stats || []), { label: "Yeni", valueKey: "", icon: "📌", color: "bg-blue-500" }])}
            className="text-xs text-primary-600 hover:text-primary-800 border border-dashed border-primary-300 w-full py-1.5 rounded"
          >+ Stat Ekle</button>
        </div>
      )}

      {widget.type === "DIVIDER" && (
        <p className="text-xs text-gray-400 italic text-center">Ayırıcı çizginin özelleştirilecek özelliği yok.</p>
      )}
    </div>
  );
};

// ─── Ana Bileşen ──────────────────────────────────────────────────────────

const ScreenBuilderPage: React.FC = () => {
  const { moduleId, screenId } = useParams<{ moduleId: string; screenId: string }>();
  const navigate = useNavigate();

  const [screen, setScreen] = useState<Screen | null>(null);
  const [widgets, setWidgets] = useState<ScreenWidget[]>([]);
  const [selectedId, setSelectedId] = useState<string | null>(null);
  const [saving, setSaving] = useState(false);
  const [saved, setSaved] = useState(false);
  const [loading, setLoading] = useState(true);

  // Drag state
  const dragFromPalette = useRef<WidgetMeta | null>(null);
  const dragFromCanvas  = useRef<string | null>(null);  // widget id
  const [dropIndex, setDropIndex] = useState<number | null>(null);

  // Ekranı yükle
  useEffect(() => {
    if (!screenId) return;
    screenApi.getById(screenId)
      .then(r => {
        const s: Screen = r.data.data;
        setScreen(s);
        if (s.template) {
          try {
            const parsed: ScreenTemplate = JSON.parse(s.template);
            setWidgets(parsed.widgets || []);
          } catch { /* empty template */ }
        }
      })
      .finally(() => setLoading(false));
  }, [screenId]);

  const selectedWidget = widgets.find(w => w.id === selectedId) || null;

  // ── Drag from palette ─────────────────────────────────────────────────
  const onPaletteDragStart = (meta: WidgetMeta) => {
    dragFromPalette.current = meta;
    dragFromCanvas.current = null;
  };

  // ── Drag from canvas ──────────────────────────────────────────────────
  const onCanvasDragStart = (id: string) => {
    dragFromCanvas.current = id;
    dragFromPalette.current = null;
  };

  // ── Drop on canvas ────────────────────────────────────────────────────
  const onDrop = (targetIndex: number) => {
    if (dragFromPalette.current) {
      // Palette'den yeni widget ekle
      const meta = dragFromPalette.current;
      const newWidget: ScreenWidget = {
        id: makeId(),
        type: meta.type,
        props: JSON.parse(JSON.stringify(meta.defaultProps)), // deep copy
      };
      const updated = [...widgets];
      updated.splice(targetIndex, 0, newWidget);
      setWidgets(updated);
      setSelectedId(newWidget.id);
    } else if (dragFromCanvas.current) {
      // Canvas içinde yeniden sırala
      const fromIndex = widgets.findIndex(w => w.id === dragFromCanvas.current);
      if (fromIndex === -1 || fromIndex === targetIndex) return;
      const updated = [...widgets];
      const [moved] = updated.splice(fromIndex, 1);
      const insertAt = fromIndex < targetIndex ? targetIndex - 1 : targetIndex;
      updated.splice(insertAt, 0, moved);
      setWidgets(updated);
    }
    dragFromPalette.current = null;
    dragFromCanvas.current = null;
    setDropIndex(null);
  };

  const onDragOver = (e: React.DragEvent, index: number) => {
    e.preventDefault();
    setDropIndex(index);
  };

  const onDragLeave = () => setDropIndex(null);

  // ── Widget güncelle ───────────────────────────────────────────────────
  const updateWidget = (props: ScreenWidget["props"]) => {
    setWidgets(prev => prev.map(w => w.id === selectedId ? { ...w, props } : w));
  };

  const deleteWidget = () => {
    setWidgets(prev => prev.filter(w => w.id !== selectedId));
    setSelectedId(null);
  };

  // ── Şablon uygula ─────────────────────────────────────────────────────
  const applyPreset = (preset: typeof PRESETS[0]) => {
    if (widgets.length > 0 && !window.confirm("Mevcut tasarım silinecek. Devam edilsin mi?")) return;
    setWidgets(preset.widgets());
    setSelectedId(null);
  };

  // ── Kaydet ────────────────────────────────────────────────────────────
  const handleSave = async () => {
    if (!screenId || !screen) return;
    setSaving(true);
    const template: ScreenTemplate = {
      version: "1.0",
      screenType: screen.screenType,
      widgets,
    };
    try {
      await screenApi.updateTemplate(screenId, JSON.stringify(template));
      setSaved(true);
      setTimeout(() => setSaved(false), 3000);
    } catch (e) {
      alert("Kaydetme başarısız.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center h-screen">
      <div className="animate-spin rounded-full h-12 w-12 border-4 border-primary-600 border-t-transparent" />
    </div>
  );

  return (
    <div className="flex flex-col h-screen bg-gray-100">

      {/* ── Üst Toolbar ─────────────────────────────────────────────── */}
      <div className="bg-white border-b border-gray-200 px-4 py-3 flex items-center gap-4 z-10">
        <button onClick={() => navigate(-1)} className="text-sm text-gray-500 hover:text-gray-800 flex items-center gap-1">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
          </svg>
          Geri
        </button>

        <div className="h-5 w-px bg-gray-200" />

        <div>
          <p className="text-sm font-semibold text-gray-900">{screen?.name || "Ekran Tasarımcısı"}</p>
          <p className="text-xs text-gray-400">{screen?.code}</p>
        </div>

        <div className="flex-1" />

        {/* Şablon ön ayarları */}
        <div className="flex items-center gap-1">
          <span className="text-xs text-gray-500 mr-1">Şablon:</span>
          {PRESETS.map(p => (
            <button
              key={p.label}
              onClick={() => applyPreset(p)}
              className="text-xs px-3 py-1.5 border border-gray-200 hover:border-primary-400 hover:bg-primary-50 rounded-lg transition flex items-center gap-1"
            >
              <span>{p.icon}</span> {p.label}
            </button>
          ))}
        </div>

        <div className="h-5 w-px bg-gray-200" />

        {/* Widget sayacı */}
        <span className="text-xs text-gray-400">{widgets.length} widget</span>

        {/* Kaydet */}
        <button
          onClick={handleSave}
          disabled={saving}
          className={`flex items-center gap-2 px-5 py-2 rounded-lg text-sm font-medium transition ${
            saved
              ? "bg-green-500 text-white"
              : "bg-primary-600 hover:bg-primary-700 text-white disabled:opacity-60"
          }`}
        >
          {saving ? (
            <span className="flex items-center gap-2"><div className="animate-spin rounded-full h-3.5 w-3.5 border-2 border-white border-t-transparent" />Kaydediliyor...</span>
          ) : saved ? (
            <span className="flex items-center gap-2">✓ Kaydedildi</span>
          ) : (
            <span className="flex items-center gap-2">
              <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7H5a2 2 0 00-2 2v9a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-3m-1 4l-3 3m0 0l-3-3m3 3V4" />
              </svg>
              Kaydet
            </span>
          )}
        </button>
      </div>

      {/* ── Ana İçerik: 3 Sütun ────────────────────────────────────── */}
      <div className="flex flex-1 overflow-hidden">

        {/* Sol Panel — Widget Paleti */}
        <div className="w-56 bg-white border-r border-gray-200 overflow-y-auto flex-shrink-0">
          <div className="p-3 border-b border-gray-100">
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide">Bileşenler</p>
          </div>
          {WIDGET_CATALOGUE.map(cat => (
            <div key={cat.category} className="p-3">
              <p className="text-xs font-semibold text-gray-400 uppercase tracking-wide mb-2">{cat.category}</p>
              <div className="space-y-1">
                {cat.items.map(meta => (
                  <div
                    key={meta.type}
                    draggable
                    onDragStart={() => onPaletteDragStart(meta)}
                    className="flex items-center gap-2.5 px-3 py-2.5 rounded-lg border border-gray-100 hover:border-primary-300 hover:bg-primary-50 cursor-grab active:cursor-grabbing transition select-none"
                  >
                    <div className="w-7 h-7 bg-gray-100 rounded-md flex items-center justify-center text-sm font-bold text-gray-600 shrink-0">
                      {meta.icon}
                    </div>
                    <div>
                      <p className="text-xs font-medium text-gray-800">{meta.label}</p>
                      <p className="text-xs text-gray-400 leading-tight">{meta.description}</p>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          ))}
        </div>

        {/* Orta Panel — Canvas */}
        <div className="flex-1 overflow-y-auto p-6">
          <div className="max-w-3xl mx-auto">

            {/* Drop Zone — en üst */}
            <div
              onDragOver={e => onDragOver(e, 0)}
              onDragLeave={onDragLeave}
              onDrop={() => onDrop(0)}
              className={`h-3 rounded transition mb-1 ${dropIndex === 0 ? "bg-primary-300" : "bg-transparent"}`}
            />

            {widgets.length === 0 ? (
              <div
                onDragOver={e => { e.preventDefault(); setDropIndex(0); }}
                onDragLeave={onDragLeave}
                onDrop={() => onDrop(0)}
                className="border-2 border-dashed border-gray-300 rounded-2xl py-20 text-center text-gray-400 hover:border-primary-300 hover:bg-primary-50/30 transition"
              >
                <div className="text-4xl mb-3">🎨</div>
                <p className="font-medium">Buraya bileşen sürükleyin</p>
                <p className="text-sm mt-1">veya üstten bir şablon seçin</p>
              </div>
            ) : (
              widgets.map((w, i) => (
                <div key={w.id}>
                  {/* Her widget'tan sonra drop zone */}
                  <div
                    draggable
                    onDragStart={() => onCanvasDragStart(w.id)}
                    onDragOver={e => onDragOver(e, i)}
                    onDragLeave={onDragLeave}
                    onDrop={() => onDrop(i)}
                    className="mb-2 cursor-move"
                  >
                    {/* Sürükleme tutma kolu */}
                    <div className="flex items-center gap-1 mb-1 opacity-0 hover:opacity-100 transition group">
                      <div className="flex-1 h-px bg-primary-100" />
                      <span className="text-xs text-gray-300 select-none px-2">⠿ sürükle</span>
                      <div className="flex-1 h-px bg-primary-100" />
                    </div>
                    <WidgetPreview
                      widget={w}
                      selected={selectedId === w.id}
                      onSelect={() => setSelectedId(w.id === selectedId ? null : w.id)}
                    />
                  </div>
                  {/* Drop zone araya */}
                  <div
                    onDragOver={e => onDragOver(e, i + 1)}
                    onDragLeave={onDragLeave}
                    onDrop={() => onDrop(i + 1)}
                    className={`h-3 rounded transition mb-1 ${dropIndex === i + 1 ? "bg-primary-300" : "bg-transparent"}`}
                  />
                </div>
              ))
            )}
          </div>
        </div>

        {/* Sağ Panel — Özellik Editörü */}
        <div className="w-72 bg-white border-l border-gray-200 flex-shrink-0 overflow-hidden flex flex-col">
          <div className="p-3 border-b border-gray-100">
            <p className="text-xs font-semibold text-gray-500 uppercase tracking-wide">
              {selectedWidget ? `${selectedWidget.type} — Özellikler` : "Özellikler"}
            </p>
          </div>
          <div className="flex-1 overflow-y-auto">
            <PropertiesPanel
              widget={selectedWidget}
              onUpdate={updateWidget}
              onDelete={deleteWidget}
            />
          </div>
        </div>

      </div>
    </div>
  );
};

export default ScreenBuilderPage;
