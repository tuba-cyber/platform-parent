/**
 * ModuleAdminPage.tsx
 *
 * Modül Yönetim Paneli.
 * Hiyerarşi: Module → Section → Screen
 *
 * Özellikler:
 * - Modül kartları (toggle ile aktif/pasif)
 * - Modül içinde bölümleri (Section) açılır/kapanır accordion
 * - Her bölümde ekranlar (Screen) listelenir
 * - Ekrana tıklayınca Screen Builder açılır
 * - Modül / Section / Screen CRUD (modal formlar)
 */

import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  moduleApi, sectionApi, screenApi,
  PlatformModule, Section, Screen, ScreenType
} from "../../api/moduleEngineApi";

// ─── Yardımcı Sabitler ────────────────────────────────────────────────────

const SCREEN_TYPE_LABELS: Record<ScreenType, string> = {
  LIST:      "Liste",
  FORM:      "Form",
  DETAIL:    "Detay",
  MAP:       "Harita",
  DASHBOARD: "Dashboard",
  CUSTOM:    "Özel",
};

const SCREEN_TYPE_COLORS: Record<ScreenType, string> = {
  LIST:      "bg-blue-100 text-blue-700",
  FORM:      "bg-green-100 text-green-700",
  DETAIL:    "bg-purple-100 text-purple-700",
  MAP:       "bg-teal-100 text-teal-700",
  DASHBOARD: "bg-orange-100 text-orange-700",
  CUSTOM:    "bg-gray-100 text-gray-600",
};

const MODULE_COLORS = [
  "#3B82F6","#10B981","#8B5CF6","#F59E0B","#EF4444",
  "#06B6D4","#84CC16","#EC4899","#6366F1","#14B8A6",
];

const inp = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500";

// ─── Modal Bileşeni ────────────────────────────────────────────────────────

const Modal: React.FC<{ title: string; onClose: () => void; children: React.ReactNode }> = ({ title, onClose, children }) => (
  <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
    <div className="bg-white rounded-2xl shadow-2xl w-full max-w-lg mx-4 overflow-hidden max-h-[90vh] overflow-y-auto">
      <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
        <h3 className="text-base font-semibold text-gray-900">{title}</h3>
        <button onClick={onClose} className="text-gray-400 hover:text-gray-600">
          <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
          </svg>
        </button>
      </div>
      <div className="p-6">{children}</div>
    </div>
  </div>
);

// ─── Modül Formu ───────────────────────────────────────────────────────────

interface ModuleFormData { name: string; code: string; description: string; icon: string; color: string; orderIndex: number; }
const emptyModule: ModuleFormData = { name: "", code: "", description: "", icon: "🧩", color: "#3B82F6", orderIndex: 0 };

const ModuleFormModal: React.FC<{
  initial?: PlatformModule | null;
  onSave: (data: ModuleFormData) => void;
  onClose: () => void;
  saving: boolean;
  error: string;
}> = ({ initial, onSave, onClose, saving, error }) => {
  const [form, setForm] = useState<ModuleFormData>(
    initial
      ? { name: initial.name, code: initial.code, description: initial.description || "", icon: initial.icon || "🧩", color: initial.color || "#3B82F6", orderIndex: initial.orderIndex }
      : emptyModule
  );
  const set = (k: keyof ModuleFormData, v: any) => setForm(f => ({ ...f, [k]: v }));

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSave(form); }} className="space-y-4">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Modül Adı *</label>
          <input value={form.name} onChange={e => set("name", e.target.value)} className={inp} required placeholder="İnsan Kaynakları" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Kod *</label>
          <input value={form.code} onChange={e => set("code", e.target.value.toUpperCase())} className={inp} required placeholder="HR" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">İkon (emoji)</label>
          <input value={form.icon} onChange={e => set("icon", e.target.value)} className={inp} placeholder="🧩" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Sıra</label>
          <input type="number" value={form.orderIndex} onChange={e => set("orderIndex", Number(e.target.value))} className={inp} />
        </div>
        <div className="col-span-2">
          <label className="block text-xs font-medium text-gray-600 mb-1">Açıklama</label>
          <input value={form.description} onChange={e => set("description", e.target.value)} className={inp} placeholder="Modül açıklaması..." />
        </div>
        <div className="col-span-2">
          <label className="block text-xs font-medium text-gray-600 mb-2">Renk</label>
          <div className="flex gap-2 flex-wrap">
            {MODULE_COLORS.map(c => (
              <button key={c} type="button" onClick={() => set("color", c)}
                className={`w-7 h-7 rounded-full border-2 transition ${form.color === c ? "border-gray-900 scale-110" : "border-transparent"}`}
                style={{ backgroundColor: c }} />
            ))}
          </div>
        </div>
      </div>
      {error && <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-2">{error}</div>}
      <div className="flex gap-3 pt-2">
        <button type="submit" disabled={saving} className="flex-1 bg-primary-600 hover:bg-primary-700 text-white py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60">
          {saving ? "Kaydediliyor..." : initial ? "Güncelle" : "Kaydet"}
        </button>
        <button type="button" onClick={onClose} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2.5 rounded-lg font-medium text-sm transition">İptal</button>
      </div>
    </form>
  );
};

// ─── Section Formu ─────────────────────────────────────────────────────────

const SectionFormModal: React.FC<{
  initial?: Section | null;
  onSave: (data: any) => void;
  onClose: () => void;
  saving: boolean;
  error: string;
}> = ({ initial, onSave, onClose, saving, error }) => {
  const [form, setForm] = useState({ name: initial?.name || "", description: initial?.description || "", icon: initial?.icon || "", orderIndex: initial?.orderIndex || 0 });
  const set = (k: string, v: any) => setForm(f => ({ ...f, [k]: v }));

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSave(form); }} className="space-y-4">
      <div>
        <label className="block text-xs font-medium text-gray-600 mb-1">Bölüm Adı *</label>
        <input value={form.name} onChange={e => set("name", e.target.value)} className={inp} required placeholder="Yönetim" />
      </div>
      <div>
        <label className="block text-xs font-medium text-gray-600 mb-1">İkon (emoji)</label>
        <input value={form.icon} onChange={e => set("icon", e.target.value)} className={inp} placeholder="📂" />
      </div>
      <div>
        <label className="block text-xs font-medium text-gray-600 mb-1">Sıra</label>
        <input type="number" value={form.orderIndex} onChange={e => set("orderIndex", Number(e.target.value))} className={inp} />
      </div>
      <div>
        <label className="block text-xs font-medium text-gray-600 mb-1">Açıklama</label>
        <input value={form.description} onChange={e => set("description", e.target.value)} className={inp} />
      </div>
      {error && <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-2">{error}</div>}
      <div className="flex gap-3 pt-2">
        <button type="submit" disabled={saving} className="flex-1 bg-primary-600 hover:bg-primary-700 text-white py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60">
          {saving ? "Kaydediliyor..." : initial ? "Güncelle" : "Kaydet"}
        </button>
        <button type="button" onClick={onClose} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2.5 rounded-lg font-medium text-sm transition">İptal</button>
      </div>
    </form>
  );
};

// ─── Screen Formu ──────────────────────────────────────────────────────────

const ScreenFormModal: React.FC<{
  initial?: Screen | null;
  onSave: (data: any) => void;
  onClose: () => void;
  saving: boolean;
  error: string;
}> = ({ initial, onSave, onClose, saving, error }) => {
  const [form, setForm] = useState({
    name: initial?.name || "",
    code: initial?.code || "",
    description: initial?.description || "",
    icon: initial?.icon || "",
    screenType: initial?.screenType || "LIST" as ScreenType,
    orderIndex: initial?.orderIndex || 0,
    requiredPermission: initial?.requiredPermission || "",
  });
  const set = (k: string, v: any) => setForm(f => ({ ...f, [k]: v }));

  return (
    <form onSubmit={(e) => { e.preventDefault(); onSave(form); }} className="space-y-4">
      <div className="grid grid-cols-2 gap-3">
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Ekran Adı *</label>
          <input value={form.name} onChange={e => set("name", e.target.value)} className={inp} required placeholder="Çalışan Listesi" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Kod *</label>
          <input value={form.code} onChange={e => set("code", e.target.value.toUpperCase())} className={inp} required placeholder="HR_EMP_LIST" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Ekran Tipi</label>
          <select value={form.screenType} onChange={e => set("screenType", e.target.value)} className={inp}>
            {(Object.keys(SCREEN_TYPE_LABELS) as ScreenType[]).map(t => (
              <option key={t} value={t}>{SCREEN_TYPE_LABELS[t]}</option>
            ))}
          </select>
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">İkon (emoji)</label>
          <input value={form.icon} onChange={e => set("icon", e.target.value)} className={inp} placeholder="📋" />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Sıra</label>
          <input type="number" value={form.orderIndex} onChange={e => set("orderIndex", Number(e.target.value))} className={inp} />
        </div>
        <div>
          <label className="block text-xs font-medium text-gray-600 mb-1">Gereken Yetki</label>
          <input value={form.requiredPermission} onChange={e => set("requiredPermission", e.target.value)} className={inp} placeholder="EMPLOYEE_VIEW" />
        </div>
        <div className="col-span-2">
          <label className="block text-xs font-medium text-gray-600 mb-1">Açıklama</label>
          <input value={form.description} onChange={e => set("description", e.target.value)} className={inp} />
        </div>
      </div>
      {error && <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-2">{error}</div>}
      <div className="flex gap-3 pt-2">
        <button type="submit" disabled={saving} className="flex-1 bg-primary-600 hover:bg-primary-700 text-white py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60">
          {saving ? "Kaydediliyor..." : initial ? "Güncelle" : "Kaydet"}
        </button>
        <button type="button" onClick={onClose} className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2.5 rounded-lg font-medium text-sm transition">İptal</button>
      </div>
    </form>
  );
};

// ─── Ekran Satırı ──────────────────────────────────────────────────────────

const ScreenRow: React.FC<{
  screen: Screen;
  moduleId: string;
  onEdit: () => void;
  onDelete: () => void;
}> = ({ screen, moduleId, onEdit, onDelete }) => {
  const navigate = useNavigate();
  return (
    <div className="flex items-center justify-between py-2.5 px-4 hover:bg-blue-50 rounded-lg transition group">
      <div className="flex items-center gap-3">
        <span className="text-lg">{screen.icon || "📄"}</span>
        <div>
          <p className="text-sm font-medium text-gray-800">{screen.name}</p>
          <div className="flex items-center gap-2 mt-0.5">
            <span className="text-xs text-gray-400 font-mono">{screen.code}</span>
            <span className={`text-xs px-1.5 py-0.5 rounded font-medium ${SCREEN_TYPE_COLORS[screen.screenType]}`}>
              {SCREEN_TYPE_LABELS[screen.screenType]}
            </span>
            {!screen.active && (
              <span className="text-xs px-1.5 py-0.5 rounded bg-red-100 text-red-600 font-medium">Pasif</span>
            )}
          </div>
        </div>
      </div>
      <div className="flex items-center gap-2 opacity-0 group-hover:opacity-100 transition">
        <button
          onClick={() => navigate(`/admin/builder/${moduleId}/${screen.id}`)}
          className="flex items-center gap-1 text-xs bg-primary-600 text-white px-3 py-1.5 rounded-lg hover:bg-primary-700 transition font-medium"
        >
          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M11 4H4a2 2 0 00-2 2v14a2 2 0 002 2h14a2 2 0 002-2v-7" />
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M18.5 2.5a2.121 2.121 0 013 3L12 15l-4 1 1-4 9.5-9.5z" />
          </svg>
          Tasarla
        </button>
        <button onClick={onEdit} className="text-xs text-gray-500 hover:text-gray-800 px-2 py-1.5">Düzenle</button>
        <button onClick={onDelete} className="text-xs text-red-400 hover:text-red-600 px-2 py-1.5">Sil</button>
      </div>
    </div>
  );
};

// ─── Section Accordion ─────────────────────────────────────────────────────

const SectionAccordion: React.FC<{
  section: Section;
  moduleId: string;
  onEditSection: () => void;
  onDeleteSection: () => void;
}> = ({ section, moduleId, onEditSection, onDeleteSection }) => {
  const [open, setOpen] = useState(false);
  const [screens, setScreens] = useState<Screen[]>([]);
  const [loadingScreens, setLoadingScreens] = useState(false);
  const [showAddScreen, setShowAddScreen] = useState(false);
  const [editingScreen, setEditingScreen] = useState<Screen | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const loadScreens = () => {
    setLoadingScreens(true);
    screenApi.getBySection(section.id)
      .then(r => setScreens(r.data.data || []))
      .finally(() => setLoadingScreens(false));
  };

  const handleToggle = () => {
    if (!open) loadScreens();
    setOpen(o => !o);
  };

  const handleSaveScreen = async (data: any) => {
    setSaving(true);
    setError("");
    try {
      if (editingScreen) {
        await screenApi.update(editingScreen.id, data);
      } else {
        await screenApi.create(section.id, data);
      }
      setShowAddScreen(false);
      setEditingScreen(null);
      loadScreens();
    } catch (e: any) {
      setError(e.response?.data?.message || "Hata oluştu");
    } finally { setSaving(false); }
  };

  const handleDeleteScreen = async (id: string) => {
    if (!window.confirm("Ekranı silmek istediğinizden emin misiniz?")) return;
    await screenApi.delete(id);
    loadScreens();
  };

  return (
    <div className="border border-gray-100 rounded-xl overflow-hidden mb-2">
      {/* Section başlığı */}
      <div
        className="flex items-center justify-between px-4 py-3 bg-gray-50 hover:bg-gray-100 cursor-pointer transition"
        onClick={handleToggle}
      >
        <div className="flex items-center gap-2">
          <svg className={`w-4 h-4 text-gray-400 transition-transform ${open ? "rotate-90" : ""}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          </svg>
          <span className="text-sm">{section.icon || "📂"}</span>
          <span className="text-sm font-medium text-gray-800">{section.name}</span>
          {!section.active && <span className="text-xs bg-red-100 text-red-600 px-1.5 py-0.5 rounded">Pasif</span>}
        </div>
        <div className="flex items-center gap-2" onClick={e => e.stopPropagation()}>
          <button onClick={onEditSection} className="text-xs text-gray-400 hover:text-gray-700 px-2 py-1">Düzenle</button>
          <button onClick={onDeleteSection} className="text-xs text-red-300 hover:text-red-500 px-2 py-1">Sil</button>
        </div>
      </div>

      {/* Screen listesi */}
      {open && (
        <div className="px-2 py-2">
          {loadingScreens ? (
            <div className="flex justify-center py-4">
              <div className="animate-spin rounded-full h-5 w-5 border-2 border-primary-600 border-t-transparent" />
            </div>
          ) : screens.length === 0 ? (
            <p className="text-center text-sm text-gray-400 py-4">Henüz ekran yok.</p>
          ) : (
            screens.map(s => (
              <ScreenRow
                key={s.id}
                screen={s}
                moduleId={moduleId}
                onEdit={() => { setEditingScreen(s); setShowAddScreen(true); }}
                onDelete={() => handleDeleteScreen(s.id)}
              />
            ))
          )}
          <button
            onClick={() => { setEditingScreen(null); setShowAddScreen(true); }}
            className="mt-2 w-full flex items-center justify-center gap-1 text-xs text-primary-600 hover:text-primary-800 border border-dashed border-primary-300 hover:border-primary-500 py-2 rounded-lg transition"
          >
            <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
            Ekran Ekle
          </button>
        </div>
      )}

      {/* Ekran form modal */}
      {showAddScreen && (
        <Modal
          title={editingScreen ? "Ekran Düzenle" : "Yeni Ekran"}
          onClose={() => { setShowAddScreen(false); setEditingScreen(null); }}
        >
          <ScreenFormModal
            initial={editingScreen}
            onSave={handleSaveScreen}
            onClose={() => { setShowAddScreen(false); setEditingScreen(null); }}
            saving={saving}
            error={error}
          />
        </Modal>
      )}
    </div>
  );
};

// ─── Modül Kartı ───────────────────────────────────────────────────────────

const ModuleCard: React.FC<{
  module: PlatformModule;
  onToggle: () => void;
  onEdit: () => void;
  onDelete: () => void;
}> = ({ module, onToggle, onEdit, onDelete }) => {
  const [expanded, setExpanded] = useState(false);
  const [sections, setSections] = useState<Section[]>([]);
  const [loadingSections, setLoadingSections] = useState(false);
  const [showAddSection, setShowAddSection] = useState(false);
  const [editingSection, setEditingSection] = useState<Section | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const loadSections = () => {
    setLoadingSections(true);
    sectionApi.getByModule(module.id)
      .then(r => setSections(r.data.data || []))
      .finally(() => setLoadingSections(false));
  };

  const handleExpand = () => {
    if (!expanded) loadSections();
    setExpanded(e => !e);
  };

  const handleSaveSection = async (data: any) => {
    setSaving(true);
    setError("");
    try {
      if (editingSection) {
        await sectionApi.update(editingSection.id, data);
      } else {
        await sectionApi.create(module.id, data);
      }
      setShowAddSection(false);
      setEditingSection(null);
      loadSections();
    } catch (e: any) {
      setError(e.response?.data?.message || "Hata oluştu");
    } finally { setSaving(false); }
  };

  const handleDeleteSection = async (id: string) => {
    if (!window.confirm("Bölümü silmek istediğinizden emin misiniz?")) return;
    await sectionApi.delete(id);
    loadSections();
  };

  return (
    <div className={`bg-white rounded-2xl border-2 shadow-sm transition ${module.active ? "border-gray-100" : "border-red-100 opacity-75"}`}>
      {/* Kart üst kısmı — renkli şerit */}
      <div className="h-1.5 rounded-t-2xl" style={{ backgroundColor: module.color || "#3B82F6" }} />

      <div className="p-5">
        {/* Kart başlığı */}
        <div className="flex items-start justify-between mb-3">
          <div className="flex items-center gap-3">
            <div className="w-11 h-11 rounded-xl flex items-center justify-center text-2xl" style={{ backgroundColor: (module.color || "#3B82F6") + "20" }}>
              {module.icon || "🧩"}
            </div>
            <div>
              <div className="flex items-center gap-2">
                <h3 className="text-base font-bold text-gray-900">{module.name}</h3>
                <span className="text-xs font-mono bg-gray-100 text-gray-500 px-1.5 py-0.5 rounded">{module.code}</span>
              </div>
              {module.description && (
                <p className="text-xs text-gray-500 mt-0.5">{module.description}</p>
              )}
            </div>
          </div>

          {/* Toggle switch */}
          <button
            onClick={onToggle}
            className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${module.active ? "bg-green-500" : "bg-gray-300"}`}
            title={module.active ? "Pasife Al" : "Aktive Et"}
          >
            <span className={`inline-block h-4 w-4 transform rounded-full bg-white shadow transition-transform ${module.active ? "translate-x-6" : "translate-x-1"}`} />
          </button>
        </div>

        {/* Alt aksiyonlar */}
        <div className="flex items-center justify-between">
          <div className="flex gap-2">
            <button onClick={onEdit} className="text-xs text-gray-400 hover:text-gray-700 border border-gray-200 hover:border-gray-400 px-2.5 py-1 rounded-lg transition">Düzenle</button>
            <button onClick={onDelete} className="text-xs text-red-400 hover:text-red-600 border border-red-100 hover:border-red-300 px-2.5 py-1 rounded-lg transition">Sil</button>
          </div>
          <button
            onClick={handleExpand}
            className="flex items-center gap-1 text-xs text-primary-600 hover:text-primary-800 font-medium"
          >
            {expanded ? "Kapat" : "Bölümler / Ekranlar"}
            <svg className={`w-3.5 h-3.5 transition-transform ${expanded ? "rotate-180" : ""}`} fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 9l-7 7-7-7" />
            </svg>
          </button>
        </div>

        {/* Genişletilmiş alan */}
        {expanded && (
          <div className="mt-4 border-t border-gray-100 pt-4">
            {loadingSections ? (
              <div className="flex justify-center py-6">
                <div className="animate-spin rounded-full h-6 w-6 border-2 border-primary-600 border-t-transparent" />
              </div>
            ) : (
              <>
                {sections.length === 0 ? (
                  <p className="text-center text-sm text-gray-400 py-4">Bu modülde henüz bölüm yok.</p>
                ) : (
                  sections.map(sec => (
                    <SectionAccordion
                      key={sec.id}
                      section={sec}
                      moduleId={module.id}
                      onEditSection={() => { setEditingSection(sec); setShowAddSection(true); }}
                      onDeleteSection={() => handleDeleteSection(sec.id)}
                    />
                  ))
                )}
                <button
                  onClick={() => { setEditingSection(null); setShowAddSection(true); }}
                  className="mt-2 w-full flex items-center justify-center gap-1.5 text-sm text-primary-600 hover:text-primary-800 border border-dashed border-primary-200 hover:border-primary-400 py-3 rounded-xl transition"
                >
                  <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
                  </svg>
                  Bölüm Ekle
                </button>
              </>
            )}
          </div>
        )}
      </div>

      {/* Section form modal */}
      {showAddSection && (
        <Modal
          title={editingSection ? "Bölüm Düzenle" : "Yeni Bölüm"}
          onClose={() => { setShowAddSection(false); setEditingSection(null); }}
        >
          <SectionFormModal
            initial={editingSection}
            onSave={handleSaveSection}
            onClose={() => { setShowAddSection(false); setEditingSection(null); }}
            saving={saving}
            error={error}
          />
        </Modal>
      )}
    </div>
  );
};

// ─── Ana Sayfa ─────────────────────────────────────────────────────────────

const ModuleAdminPage: React.FC = () => {
  const [modules, setModules] = useState<PlatformModule[]>([]);
  const [loading, setLoading] = useState(true);
  const [showAddModule, setShowAddModule] = useState(false);
  const [editingModule, setEditingModule] = useState<PlatformModule | null>(null);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = () => {
    setLoading(true);
    moduleApi.getAll()
      .then(r => setModules(r.data.data || []))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const handleToggle = async (id: string) => {
    await moduleApi.toggle(id);
    load();
  };

  const handleSaveModule = async (data: any) => {
    setSaving(true);
    setError("");
    try {
      if (editingModule) {
        await moduleApi.update(editingModule.id, data);
      } else {
        await moduleApi.create(data);
      }
      setShowAddModule(false);
      setEditingModule(null);
      load();
    } catch (e: any) {
      setError(e.response?.data?.message || "Hata oluştu");
    } finally { setSaving(false); }
  };

  const handleDeleteModule = async (id: string) => {
    if (!window.confirm("Modülü silmek istediğinizden emin misiniz? Tüm bölüm ve ekranlar da silinir.")) return;
    await moduleApi.delete(id);
    load();
  };

  const activeCount = modules.filter(m => m.active).length;

  return (
    <div>
      {/* Başlık */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Modül Yönetimi</h1>
          <p className="text-gray-500 text-sm mt-1">
            {modules.length} modül kayıtlı · {activeCount} aktif
          </p>
        </div>
        <button
          onClick={() => { setEditingModule(null); setShowAddModule(true); }}
          className="flex items-center gap-2 bg-primary-600 hover:bg-primary-700 text-white px-4 py-2.5 rounded-lg text-sm font-medium transition"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Yeni Modül
        </button>
      </div>

      {/* Bilgi kutusu */}
      <div className="bg-blue-50 border border-blue-200 rounded-xl px-4 py-3 mb-6 flex items-start gap-3">
        <svg className="w-5 h-5 text-blue-500 mt-0.5 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
        </svg>
        <p className="text-sm text-blue-700">
          Her modül → bölümler (section) → ekranlar (screen) hiyerarşisinden oluşur.
          "Tasarla" butonu ile sürükle-bırak ekran tasarımcısını açabilirsiniz.
        </p>
      </div>

      {/* Modül kartları */}
      {loading ? (
        <div className="flex justify-center py-20">
          <div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-600 border-t-transparent" />
        </div>
      ) : modules.length === 0 ? (
        <div className="text-center py-20 text-gray-400">
          <div className="text-5xl mb-4">🧩</div>
          <p className="font-medium text-gray-600">Henüz modül yok</p>
          <p className="text-sm mt-1">Yukarıdaki butona tıklayarak ilk modülü ekleyin.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-5">
          {modules.map(m => (
            <ModuleCard
              key={m.id}
              module={m}
              onToggle={() => handleToggle(m.id)}
              onEdit={() => { setEditingModule(m); setShowAddModule(true); }}
              onDelete={() => handleDeleteModule(m.id)}
            />
          ))}
        </div>
      )}

      {/* Modül form modal */}
      {showAddModule && (
        <Modal
          title={editingModule ? "Modül Düzenle" : "Yeni Modül"}
          onClose={() => { setShowAddModule(false); setEditingModule(null); }}
        >
          <ModuleFormModal
            initial={editingModule}
            onSave={handleSaveModule}
            onClose={() => { setShowAddModule(false); setEditingModule(null); }}
            saving={saving}
            error={error}
          />
        </Modal>
      )}
    </div>
  );
};

export default ModuleAdminPage;
