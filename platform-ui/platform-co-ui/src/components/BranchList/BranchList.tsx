import React, { useEffect, useState } from "react";
import { branchApi, Branch } from "../../api/companyApi";

interface Props {
  companyId: string;
}

interface FormState {
  name: string; code: string; phone: string; email: string;
  address: string; city: string; district: string; headquarters: boolean;
}
const emptyForm: FormState = {
  name: "", code: "", phone: "", email: "",
  address: "", city: "", district: "", headquarters: false,
};

const BranchList: React.FC<Props> = ({ companyId }) => {
  const [branches, setBranches] = useState<Branch[]>([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [editing, setEditing] = useState<Branch | null>(null);
  const [form, setForm] = useState<FormState>(emptyForm);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  const load = () => {
    if (!companyId) { setLoading(false); return; }
    setLoading(true);
    branchApi.getByCompany(companyId)
      .then((r) => setBranches(r.data.data || []))
      .catch(() => setBranches([]))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, [companyId]);

  const openNew = () => {
    setEditing(null);
    setForm(emptyForm);
    setError("");
    setShowModal(true);
  };

  const openEdit = (b: Branch) => {
    setEditing(b);
    setForm({
      name: b.name, code: b.code,
      phone: b.phone || "", email: b.email || "",
      address: b.address || "", city: b.city || "",
      district: b.district || "", headquarters: b.headquarters,
    });
    setError("");
    setShowModal(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form, companyId };
      if (editing) {
        await branchApi.update(editing.id, payload);
      } else {
        await branchApi.create(payload);
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
    if (!window.confirm("Şubeyi silmek istediğinizden emin misiniz?")) return;
    try {
      await branchApi.delete(id);
      load();
    } catch {
      alert("İşlem başarısız.");
    }
  };

  const inp = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500";

  return (
    <div className="mt-6">
      <div className="flex items-center justify-between mb-4">
        <h3 className="text-base font-semibold text-gray-800">Şubeler / Lokasyonlar</h3>
        <button
          onClick={openNew}
          className="flex items-center gap-1.5 bg-primary-600 hover:bg-primary-700 text-white px-3 py-1.5 rounded-lg text-xs font-medium transition"
        >
          <svg className="w-3.5 h-3.5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Şube Ekle
        </button>
      </div>

      {loading ? (
        <div className="flex items-center justify-center py-10">
          <div className="animate-spin rounded-full h-6 w-6 border-4 border-primary-600 border-t-transparent" />
        </div>
      ) : branches.length === 0 ? (
        <div className="text-center py-10 text-gray-400 border border-dashed border-gray-200 rounded-xl">
          <div className="text-3xl mb-2">🏪</div>
          <p className="text-sm">Henüz şube kaydı yok.</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
          {branches.map((b) => (
            <div key={b.id} className="bg-gray-50 border border-gray-200 rounded-xl p-4">
              <div className="flex items-start justify-between mb-2">
                <div>
                  <div className="flex items-center gap-2">
                    <span className="font-semibold text-gray-900 text-sm">{b.name}</span>
                    {b.headquarters && (
                      <span className="bg-yellow-100 text-yellow-700 text-xs px-1.5 py-0.5 rounded font-medium">
                        Merkez
                      </span>
                    )}
                  </div>
                  <span className="text-xs text-gray-500 font-mono bg-gray-200 px-1.5 py-0.5 rounded mt-1 inline-block">
                    {b.code}
                  </span>
                </div>
                <span className={`text-xs px-2 py-0.5 rounded-full font-medium ${
                  b.active ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"
                }`}>
                  {b.active ? "Aktif" : "Pasif"}
                </span>
              </div>

              {(b.city || b.address) && (
                <p className="text-xs text-gray-500 mb-2">
                  📍 {[b.address, b.district, b.city].filter(Boolean).join(", ")}
                </p>
              )}
              {b.phone && <p className="text-xs text-gray-500">📞 {b.phone}</p>}

              <div className="flex gap-2 mt-3">
                <button
                  onClick={() => openEdit(b)}
                  className="text-xs text-primary-600 hover:text-primary-800 font-medium"
                >
                  Düzenle
                </button>
                <span className="text-gray-300">·</span>
                <button
                  onClick={() => handleDelete(b.id)}
                  className="text-xs text-red-400 hover:text-red-600"
                >
                  Sil
                </button>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Modal */}
      {showModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/40 backdrop-blur-sm">
          <div className="bg-white rounded-2xl shadow-2xl w-full max-w-md mx-4 overflow-hidden max-h-[90vh] overflow-y-auto">
            <div className="flex items-center justify-between px-6 py-4 border-b border-gray-100">
              <h3 className="text-base font-semibold text-gray-900">
                {editing ? "Şube Düzenle" : "Yeni Şube"}
              </h3>
              <button onClick={() => setShowModal(false)} className="text-gray-400 hover:text-gray-600">
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
                </svg>
              </button>
            </div>

            <form onSubmit={handleSubmit} className="p-6 space-y-4">
              <div className="grid grid-cols-2 gap-3">
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-600 mb-1">Şube Adı *</label>
                  <input value={form.name} onChange={(e) => setForm((f) => ({ ...f, name: e.target.value }))}
                    className={inp} required placeholder="İstanbul Merkez Şube" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Kod *</label>
                  <input value={form.code} onChange={(e) => setForm((f) => ({ ...f, code: e.target.value.toUpperCase() }))}
                    className={inp} required placeholder="IST-01" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Şehir</label>
                  <input value={form.city} onChange={(e) => setForm((f) => ({ ...f, city: e.target.value }))}
                    className={inp} placeholder="İstanbul" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">İlçe</label>
                  <input value={form.district} onChange={(e) => setForm((f) => ({ ...f, district: e.target.value }))}
                    className={inp} placeholder="Kadıköy" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-600 mb-1">Telefon</label>
                  <input value={form.phone} onChange={(e) => setForm((f) => ({ ...f, phone: e.target.value }))}
                    className={inp} placeholder="0212 000 00 00" />
                </div>
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-600 mb-1">Adres</label>
                  <input value={form.address} onChange={(e) => setForm((f) => ({ ...f, address: e.target.value }))}
                    className={inp} placeholder="Cadde, Sokak, No" />
                </div>
                <div className="col-span-2">
                  <label className="block text-xs font-medium text-gray-600 mb-1">E-posta</label>
                  <input type="email" value={form.email} onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
                    className={inp} placeholder="sube@sirket.com" />
                </div>
                <div className="col-span-2">
                  <label className="flex items-center gap-2 cursor-pointer">
                    <input
                      type="checkbox"
                      checked={form.headquarters}
                      onChange={(e) => setForm((f) => ({ ...f, headquarters: e.target.checked }))}
                      className="w-4 h-4 rounded border-gray-300 text-primary-600"
                    />
                    <span className="text-sm text-gray-700 font-medium">Merkez şube olarak işaretle</span>
                  </label>
                </div>
              </div>

              {error && (
                <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-2">
                  {error}
                </div>
              )}

              <div className="flex gap-3 pt-2">
                <button type="submit" disabled={saving}
                  className="flex-1 bg-primary-600 hover:bg-primary-700 text-white py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60">
                  {saving ? "Kaydediliyor..." : editing ? "Güncelle" : "Kaydet"}
                </button>
                <button type="button" onClick={() => setShowModal(false)}
                  className="flex-1 bg-gray-100 hover:bg-gray-200 text-gray-700 py-2.5 rounded-lg font-medium text-sm transition">
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

export default BranchList;
