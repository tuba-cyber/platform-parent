import React, { useEffect, useState } from "react";
import { departmentApi, Department } from "../../api/hrApi";

const getCompanyId = (): string => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.user?.companyId || "";
  } catch { return ""; }
};

interface FormState {
  name: string; code: string; description: string; parentDepartmentId: string;
}
const emptyForm: FormState = { name: "", code: "", description: "", parentDepartmentId: "" };

const DepartmentList: React.FC = () => {
  const [departments, setDepartments] = useState<Department[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  // Modal state
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<Department | null>(null);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const companyId = getCompanyId();

  const load = () => {
    if (!companyId) { setLoading(false); return; }
    setLoading(true);
    departmentApi.getByCompany(companyId)
      .then((r) => setDepartments(r.data.data || []))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const filtered = departments.filter(
    (d) =>
      d.name.toLowerCase().includes(search.toLowerCase()) ||
      d.code.toLowerCase().includes(search.toLowerCase())
  );

  const openNew = () => {
    setEditing(null);
    setForm(emptyForm);
    setError("");
    setShowModal(true);
  };

  const openEdit = (d: Department) => {
    setEditing(d);
    setForm({
      name: d.name, code: d.code,
      description: d.description || "",
      parentDepartmentId: d.parentDepartmentId || "",
    });
    setError("");
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form, companyId, parentDepartmentId: form.parentDepartmentId || null };
      if (editing) {
        await departmentApi.update(editing.id, payload);
      } else {
        await departmentApi.create(payload);
      }
      setShowModal(false);
      load();
    } catch (err: any) {
      setError(err.response?.data?.message || "Bir hata oluştu.");
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (id: string) => {
    if (!window.confirm("Departmanı pasife almak istediğinizden emin misiniz?")) return;
    try {
      await departmentApi.delete(id);
      load();
    } catch {
      alert("İşlem başarısız.");
    }
  };

  const inp = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500";

  // Build parent lookup map for display
  const parentMap: Record<string, string> = {};
  departments.forEach((d) => { parentMap[d.id] = d.name; });

  return (
    <div>
      {/* Başlık */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h2 className="text-xl font-bold text-gray-900">Departmanlar</h2>
          <p className="text-gray-500 text-sm mt-0.5">{departments.length} departman kayıtlı</p>
        </div>
        <button
          onClick={openNew}
          className="flex items-center gap-2 bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition"
        >
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Yeni Departman
        </button>
      </div>

      {/* Arama */}
      <div className="mb-4">
        <input
          type="text"
          placeholder="Departman adı veya kodu ile ara..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500"
        />
      </div>

      {/* Tablo */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center py-16">
            <div className="animate-spin rounded-full h-8 w-8 border-4 border-primary-600 border-t-transparent" />
          </div>
        ) : filtered.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <div className="text-4xl mb-3">🗂️</div>
            <p>Henüz departman kaydı yok.</p>
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                {["Departman Adı", "Kod", "Üst Departman", "Açıklama", "Durum", ""].map((h) => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wide">
                    {h}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {filtered.map((d) => (
                <tr key={d.id} className="hover:bg-gray-50 transition">
                  <td className="px-4 py-3 font-medium text-gray-900">{d.name}</td>
                  <td className="px-4 py-3">
                    <span className="bg-gray-100 text-gray-700 px-2 py-0.5 rounded text-xs font-mono">{d.code}</span>
                  </td>
                  <td className="px-4 py-3 text-gray-500 text-xs">
                    {d.parentDepartmentId ? parentMap[d.parentDepartmentId] || "—" : "—"}
                  </td>
                  <td className="px-4 py-3 text-gray-600 max-w-xs truncate">{d.description || "—"}</td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${
                      d.active ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
                    }`}>
                      {d.active ? "Aktif" : "Pasif"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-right flex items-center justify-end gap-3">
                    <button
                      onClick={() => openEdit(d)}
                      className="text-primary-600 hover:text-primary-800 text-xs font-medium"
                    >
                      Düzenle
                    </button>
                    <button
                      onClick={() => handleDelete(d.id)}
                      className="text-red-400 hover:text-red-600 text-xs"
                    >
                      Pasife Al
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h3 className="text-base font-semibold text-gray-900">
                {editing ? "Departman Düzenle" : "Yeni Departman"}
              </h3>
              <button onClick={() => setShowModal(false)} className="text-gray-400 hover:text-gray-600">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Departman Adı *</label>
                <input
                  value={form.name}
                  onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                  className={inp} required placeholder="İnsan Kaynakları"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Kod *</label>
                <input
                  value={form.code}
                  onChange={(e) => setForm((f) => ({ ...f, code: e.target.value.toUpperCase() }))}
                  className={inp} required placeholder="IK"
                />
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Üst Departman</label>
                <select
                  value={form.parentDepartmentId}
                  onChange={(e) => setForm((f) => ({ ...f, parentDepartmentId: e.target.value }))}
                  className={inp}
                >
                  <option value="">— Yok (Ana Departman) —</option>
                  {departments
                    .filter((d) => d.active && d.id !== editing?.id)
                    .map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                </select>
              </div>
              <div>
                <label className="block text-xs font-medium text-gray-600 mb-1">Açıklama</label>
                <textarea
                  value={form.description}
                  onChange={(e) => setForm((f) => ({ ...f, description: e.target.value }))}
                  className={`${inp} h-20 resize-none`}
                  placeholder="Departman hakkında kısa açıklama..."
                />
              </div>

              {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-2">
                  {error}
                </div>
              )}

              <div className="flex gap-3 pt-2">
                <button
                  type="submit"
                  disabled={saving}
                  className="flex-1 bg-primary-600 hover:bg-primary-700 text-white py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60"
                >
                  {saving ? "Kaydediliyor..." : editing ? "Güncelle" : "Kaydet"}
                </button>
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2.5 rounded-lg font-medium text-sm transition"
                >
                  İptal
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};

export default DepartmentList;
