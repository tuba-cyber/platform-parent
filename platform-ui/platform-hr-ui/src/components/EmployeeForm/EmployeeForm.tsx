import React, { useEffect, useState } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { employeeApi } from "../../api/hrApi";

const getCompanyId = (): string => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.user?.companyId || "";
  } catch {
    return "";
  }
};

const EMPLOYMENT_TYPES = [
  { value: "TAM_ZAMANLI", label: "Tam Zamanlı" },
  { value: "YARI_ZAMANLI", label: "Yarı Zamanlı" },
  { value: "SOZLESMELI", label: "Sözleşmeli" },
  { value: "STAJYER", label: "Stajyer" },
  { value: "PART_TIME", label: "Part-Time" },
  { value: "REMOTE", label: "Uzaktan" },
];

const GENDERS = [
  { value: "ERKEK", label: "Erkek" },
  { value: "KADIN", label: "Kadın" },
  { value: "BELIRTILMEMIS", label: "Belirtilmemiş" },
];

const EDUCATION_LEVELS = [
  { value: "ILKOKUL", label: "İlkokul" },
  { value: "ORTAOKUL", label: "Ortaokul" },
  { value: "LISE", label: "Lise" },
  { value: "ON_LISANS", label: "Ön Lisans" },
  { value: "LISANS", label: "Lisans" },
  { value: "YUKSEK_LISANS", label: "Yüksek Lisans" },
  { value: "DOKTORA", label: "Doktora" },
];

const empty: any = {
  companyId: getCompanyId(), departmentId: "", positionId: "",
  employeeNumber: "", firstName: "", lastName: "",
  nationalId: "", birthDate: "", gender: "BELIRTILMEMIS",
  phone: "", personalEmail: "", workEmail: "",
  address: "", city: "", district: "",
  startDate: "", employmentType: "TAM_ZAMANLI",
  educationLevel: "LISANS", school: "", schoolDepartment: "", graduationYear: "",
  emergencyContactName: "", emergencyContactPhone: "", emergencyContactRelation: "",
  annualLeaveDays: 14, performanceNotes: "", generalNotes: "",
};

const EmployeeForm: React.FC = () => {
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const isEdit = !!id && id !== "new";

  const [form, setForm] = useState<any>(empty);
  const [loading, setLoading] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    if (isEdit) {
      setLoading(true);
      employeeApi.getById(id!)
        .then((r) => setForm({ ...empty, ...r.data.data }))
        .catch(() => setError("Çalışan bilgileri yüklenemedi."))
        .finally(() => setLoading(false));
    }
  }, [id]);

  const set = (key: string, val: any) => setForm((f: any) => ({ ...f, [key]: val }));

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const payload = { ...form, graduationYear: form.graduationYear ? Number(form.graduationYear) : null };
      if (isEdit) await employeeApi.update(id!, payload);
      else        await employeeApi.create(payload);
      navigate("/hr/employees");
    } catch (err: any) {
      setError(err.response?.data?.message || "Bir hata oluştu.");
    } finally { setSaving(false); }
  };

  if (loading) return <div className="flex items-center justify-center h-64"><div className="animate-spin rounded-full h-10 w-10 border-4 border-primary-600 border-t-transparent" /></div>;

  return (
    <div className="max-w-3xl">
      <div className="mb-6">
        <button onClick={() => navigate("/hr/employees")} className="text-sm text-gray-500 hover:text-gray-800 flex items-center gap-1 mb-2">← Geri</button>
        <h1 className="text-2xl font-bold text-gray-900">{isEdit ? "Çalışan Düzenle" : "Yeni Çalışan"}</h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-5">
        <Section title="Kişisel Bilgiler">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Ad *"><input value={form.firstName} onChange={(e) => set("firstName", e.target.value)} className={inp} required /></Field>
            <Field label="Soyad *"><input value={form.lastName} onChange={(e) => set("lastName", e.target.value)} className={inp} required /></Field>
            <Field label="TC Kimlik No"><input value={form.nationalId} onChange={(e) => set("nationalId", e.target.value)} className={inp} maxLength={11} /></Field>
            <Field label="Doğum Tarihi"><input type="date" value={form.birthDate} onChange={(e) => set("birthDate", e.target.value)} className={inp} /></Field>
            <Field label="Cinsiyet">
              <select value={form.gender} onChange={(e) => set("gender", e.target.value)} className={inp}>
                {GENDERS.map((g) => <option key={g.value} value={g.value}>{g.label}</option>)}
              </select>
            </Field>
            <Field label="Telefon"><input value={form.phone} onChange={(e) => set("phone", e.target.value)} className={inp} /></Field>
            <Field label="Kişisel E-posta"><input type="email" value={form.personalEmail} onChange={(e) => set("personalEmail", e.target.value)} className={inp} /></Field>
            <Field label="Şirket E-postası"><input type="email" value={form.workEmail} onChange={(e) => set("workEmail", e.target.value)} className={inp} /></Field>
            <Field label="Şehir"><input value={form.city} onChange={(e) => set("city", e.target.value)} className={inp} /></Field>
            <Field label="İlçe"><input value={form.district} onChange={(e) => set("district", e.target.value)} className={inp} /></Field>
          </div>
        </Section>

        <Section title="İş Bilgileri">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Sicil No"><input value={form.employeeNumber} onChange={(e) => set("employeeNumber", e.target.value)} className={inp} /></Field>
            <Field label="İşe Başlama Tarihi"><input type="date" value={form.startDate} onChange={(e) => set("startDate", e.target.value)} className={inp} /></Field>
            <Field label="Çalışma Tipi">
              <select value={form.employmentType} onChange={(e) => set("employmentType", e.target.value)} className={inp}>
                {EMPLOYMENT_TYPES.map((t) => <option key={t.value} value={t.value}>{t.label}</option>)}
              </select>
            </Field>
            <Field label="Yıllık İzin (gün)"><input type="number" value={form.annualLeaveDays} onChange={(e) => set("annualLeaveDays", Number(e.target.value))} className={inp} /></Field>
          </div>
        </Section>

        <Section title="Eğitim Bilgileri">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Öğrenim Seviyesi">
              <select value={form.educationLevel} onChange={(e) => set("educationLevel", e.target.value)} className={inp}>
                {EDUCATION_LEVELS.map((l) => <option key={l.value} value={l.value}>{l.label}</option>)}
              </select>
            </Field>
            <Field label="Mezuniyet Yılı"><input type="number" value={form.graduationYear} onChange={(e) => set("graduationYear", e.target.value)} className={inp} placeholder="2018" /></Field>
            <Field label="Okul"><input value={form.school} onChange={(e) => set("school", e.target.value)} className={inp} /></Field>
            <Field label="Bölüm"><input value={form.schoolDepartment} onChange={(e) => set("schoolDepartment", e.target.value)} className={inp} /></Field>
          </div>
        </Section>

        <Section title="Acil Durum Kişisi">
          <div className="grid grid-cols-2 gap-4">
            <Field label="Ad Soyad"><input value={form.emergencyContactName} onChange={(e) => set("emergencyContactName", e.target.value)} className={inp} /></Field>
            <Field label="Telefon"><input value={form.emergencyContactPhone} onChange={(e) => set("emergencyContactPhone", e.target.value)} className={inp} /></Field>
            <Field label="Yakınlık Derecesi"><input value={form.emergencyContactRelation} onChange={(e) => set("emergencyContactRelation", e.target.value)} className={inp} placeholder="Eş, Anne, Baba..." /></Field>
          </div>
        </Section>

        <Section title="Notlar">
          <div className="space-y-3">
            <Field label="Performans Notu"><textarea value={form.performanceNotes} onChange={(e) => set("performanceNotes", e.target.value)} className={`${inp} h-24 resize-none`} /></Field>
            <Field label="Genel Not"><textarea value={form.generalNotes} onChange={(e) => set("generalNotes", e.target.value)} className={`${inp} h-24 resize-none`} /></Field>
          </div>
        </Section>

        {error && <div className="bg-red-50 border border-red-200 text-red-700 text-sm rounded-lg px-4 py-3">{error}</div>}

        <div className="flex gap-3">
          <button type="submit" disabled={saving}
            className="bg-primary-600 hover:bg-primary-700 text-white px-6 py-2.5 rounded-lg font-medium text-sm transition disabled:opacity-60">
            {saving ? "Kaydediliyor..." : isEdit ? "Güncelle" : "Kaydet"}
          </button>
          <button type="button" onClick={() => navigate("/hr/employees")}
            className="bg-gray-100 hover:bg-gray-200 text-gray-700 px-6 py-2.5 rounded-lg font-medium text-sm transition">İptal</button>
        </div>
      </form>
    </div>
  );
};

const inp = "w-full px-3 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500";
const Section: React.FC<{ title: string; children: React.ReactNode }> = ({ title, children }) => (
  <div className="bg-white rounded-xl border border-gray-100 p-5 shadow-sm">
    <h3 className="text-sm font-semibold text-gray-700 mb-4 pb-2 border-b border-gray-100">{title}</h3>
    {children}
  </div>
);
const Field: React.FC<{ label: string; children: React.ReactNode }> = ({ label, children }) => (
  <div><label className="block text-xs font-medium text-gray-600 mb-1">{label}</label>{children}</div>
);

export default EmployeeForm;
