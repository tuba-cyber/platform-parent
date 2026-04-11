import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { companyApi, CompanyRequest } from "../../api/companyApi";

const COMPANY_TYPES = [
  { value: "AS",        label: "Anonim Şirket" },
  { value: "LTD",       label: "Limited Şirket" },
  { value: "SAHIS",     label: "Şahıs Şirketi" },
  { value: "KAMU",      label: "Kamu Kurumu" },
  { value: "BELEDIYE",  label: "Belediye" },
  { value: "UNIVERSITE",label: "Üniversite" },
  { value: "DIGER",     label: "Diğer" },
];

const empty: CompanyRequest = {
  name: "", code: "", email: "", phone: "", website: "",
  address: "", city: "", district: "", postalCode: "", country: "Türkiye",
  companyType: "AS", sector: "", taxNumber: "", taxOffice: "",
  foundedYear: undefined, description: "",
};

const CompanyForm: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const isEdit = !!id && id !== "new";

  const [form, setForm] = useState<CompanyRequest>(empty);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (isEdit) {
      setLoading(true);
      companyApi.getById(id!)
        .then((r) => setForm(r.data.data))
        .catch(() => setError("Şirket bilgileri yüklenemedi."))
        .finally(() => setLoading(false));
    }
  }, [id]);

  const set = (key: keyof CompanyRequest, val: any) =>
    setForm((f) => ({ ...f, [key]: val }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      if (isEdit) {
        await companyApi.update(id!, form);
      } else {
        await companyApi.create(form);
      }
      navigate("/companies");
    } catch (err: any) {
      setError(err.response?.data?.message || "Bir hata oluştu.");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="flex items-center justify-center h-64">
      <div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-600 border-t-transparent" />
    </div>
  );

  return (
    <div className="max-w-3xl">
      <div className="mb-6">
        <button onClick={() => navigate("/companies")} className="text-sm text-gray-500 hover:text-gray-800 flex items-center gap-1 mb-2">
          ← Geri
        </button>
        <h1 className="text-2xl font-bold text-gray-900">
          {isEdit ? "Şirket Düzenle" : "Yeni Şirket"}
        </h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-6">
        {/* Temel Bilgiler */}
        <Section title="Temel Bilgiler">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Şirket Adı *" required>
              <input value={form.name} onChange={(e) => set("name", e.target.value)}
                className={input} placeholder="ABC Teknoloji A.Ş." required />
            </Field>
            <Field label="Şirket Kodu *" required>
              <input value={form.code} onChange={(e) => set("code", e.target.value.toUpperCase())}
                className={input} placeholder="ABC" required />
            </Field>
            <Field label="Şirket Türü">
              <select value={form.companyType} onChange={(e) => set("companyType", e.target.value)} className={input}>
                {COMPANY_TYPES.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
              </select>
            </Field>
            <Field label="Sektör">
              <input value={form.sector} onChange={(e) => set("sector", e.target.value)} className={input} placeholder="Teknoloji" />
            </Field>
            <Field label="Kuruluş Yılı">
              <input type="number" value={form.foundedYear || ""} onChange={(e) => set("foundedYear", Number(e.target.value))}
                className={input} placeholder="2020" />
            </Field>
          </div>
        </Section>

        {/* İletişim */}
        <Section title="İletişim Bilgileri">
          <div className="grid grid-cols-2 gap-4">
            <Field label="E-posta">
              <input type="email" value={form.email} onChange={(e) => set("email", e.target.value)} className={input} placeholder="info@sirket.com" />
            </Field>
            <Field label="Telefon">
              <input value={form.phone} onChange={(e) => set("phone", e.target.value)} className={input} placeholder="0212 000 00 00" />
            </Field>
            <Field label="Web Sitesi" className="col-span-2">
              <input value={form.website} onChange={(e) => set("website", e.target.value)} className={input} placeholder="https://sirket.com" />
            </Field>
          </div>
        </Section>

        {/* Adres */}
        <Section title="Adres">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Adres" className="col-span-2">
              <input value={form.address} onChange={(e) => set("address", e.target.value)} className={input} placeholder="Cadde, Sokak, No" />
            </Field>
            <Field label="Şehir">
              <input value={form.city} onChange={(e) => set("city", e.target.value)} className={input} placeholder="İstanbul" />
            </Field>
            <Field label="İlçe">
              <input value={form.district} onChange={(e) => set("district", e.target.value)} className={input} placeholder="Beşiktaş" />
            </Field>
            <Field label="Posta Kodu">
              <input value={form.postalCode} onChange={(e) => set("postalCode", e.target.value)} className={input} placeholder="34330" />
            </Field>
            <Field label="Ülke">
              <input value={form.country} onChange={(e) => set("country", e.target.value)} className={input} />
            </Field>
          </div>
        </Section>

        {/* Vergi */}
        <Section title="Vergi Bilgileri">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Vergi Numarası">
              <input value={form.taxNumber} onChange={(e) => set("taxNumber", e.target.value)} className={input} placeholder="1234567890" />
            </Field>
            <Field label="Vergi Dairesi">
              <input value={form.taxOffice} onChange={(e) => set("taxOffice", e.target.value)} className={input} placeholder="Beşiktaş" />
            </Field>
          </div>
        </Section>

        {error && <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3">{error}</div>}

        <div className="flex gap-3">
          <button type="submit" disabled={saving}
            className="bg-primary-600 hover:bg-primary-700 text-white px-6 py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60">
            {saving ? "Kaydediliyor..." : isEdit ? "Güncelle" : "Kaydet"}
          </button>
          <button type="button" onClick={() => navigate("/companies")}
            className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-6 py-2.5 rounded-lg font-medium text-sm transition">
            İptal
          </button>
        </div>
      </form>
    </div>
  );
};

// Yardımcı bileşenler
const input = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500";

const Section: React.FC<{ title: string; children: React.ReactNode }> = ({ title, children }) => (
  <div className="bg-white rounded-xl border border-gray-100 p-5 shadow-sm">
    <h3 className="text-sm font-semibold text-gray-700 mb-4 pb-2 border-b border-gray-100">{title}</h3>
    {children}
  </div>
);

const Field: React.FC<{ label: string; children: React.ReactNode; required?: boolean; className?: string }> = ({ label, children, required, className }) => (
  <div className={className}>
    <label className="block text-xs font-medium text-gray-600 mb-1">
      {label}{required && <span className="text-red-500 ml-0.5">*</span>}
    </label>
    {children}
  </div>
);

export default CompanyForm;
