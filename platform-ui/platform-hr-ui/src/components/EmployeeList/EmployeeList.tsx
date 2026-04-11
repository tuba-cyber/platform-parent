import React, { useEffect, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { employeeApi, Employee } from "../../api/hrApi";

const STATUS_COLORS: Record<string, string> = {
  AKTIF:           "bg-green-100 text-green-700",
  PASIF:           "bg-gray-100 text-gray-600",
  IZINDE:          "bg-yellow-100 text-yellow-700",
  ISTIFA:          "bg-orange-100 text-orange-700",
  ISTEN_CIKARILDI: "bg-red-100 text-red-700",
  EMEKLI:          "bg-blue-100 text-blue-700",
};

const getCompanyId = (): string => {
  try {
    const auth = JSON.parse(localStorage.getItem("platform-auth") || "{}");
    return auth?.state?.user?.companyId || "";
  } catch {
    return "";
  }
};

const EmployeeList: React.FC = () => {
  const navigate = useNavigate();
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [loading, setLoading] = useState(true);
  const [search, setSearch] = useState("");

  const load = () => {
    const companyId = getCompanyId();
    if (!companyId) { setLoading(false); return; }
    setLoading(true);
    employeeApi.getByCompany(companyId)
      .then((r) => setEmployees(r.data.data || []))
      .finally(() => setLoading(false));
  };

  useEffect(() => { load(); }, []);

  const filtered = employees.filter((e) =>
    e.fullName.toLowerCase().includes(search.toLowerCase()) ||
    (e.employeeNumber || "").toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-gray-900">Çalışanlar</h1>
          <p className="text-gray-500 text-sm mt-1">{employees.length} çalışan kayıtlı</p>
        </div>
        <button onClick={() => navigate("new")}
          className="flex items-center gap-2 bg-primary-600 hover:bg-primary-700 text-white px-4 py-2 rounded-lg text-sm font-medium transition">
          <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
          </svg>
          Yeni Çalışan
        </button>
      </div>

      <div className="mb-4">
        <input type="text" placeholder="Ad, soyad veya sicil no ile ara..."
          value={search} onChange={(e) => setSearch(e.target.value)}
          className="w-full max-w-sm px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-primary-500" />
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-gray-100 overflow-hidden">
        {loading ? (
          <div className="flex items-center justify-center py-16">
            <div className="animate-spin rounded-full h-8 w-8 border-4 border-primary-600 border-t-transparent" />
          </div>
        ) : filtered.length === 0 ? (
          <div className="text-center py-16 text-gray-400">
            <svg className="w-12 h-12 mx-auto mb-3 opacity-30" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={1.5} d="M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0z" />
            </svg>
            <p>Henüz çalışan kaydı yok.</p>
          </div>
        ) : (
          <table className="w-full text-sm">
            <thead className="bg-gray-50 border-b border-gray-100">
              <tr>
                {["Ad Soyad", "Sicil No", "Şehir", "Çalışma Tipi", "İzin Bakiyesi", "Durum", ""].map((h) => (
                  <th key={h} className="px-4 py-3 text-left text-xs font-semibold text-gray-500 uppercase tracking-wide">{h}</th>
                ))}
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-50">
              {filtered.map((e) => (
                <tr key={e.id} className="hover:bg-gray-50 transition">
                  <td className="px-4 py-3">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-full bg-primary-100 text-primary-700 flex items-center justify-center text-xs font-bold">
                        {e.firstName.charAt(0)}{e.lastName.charAt(0)}
                      </div>
                      <div>
                        <p className="font-medium text-gray-900">{e.fullName}</p>
                        <p className="text-xs text-gray-500">{e.workEmail || "—"}</p>
                      </div>
                    </div>
                  </td>
                  <td className="px-4 py-3">
                    <span className="bg-gray-100 text-gray-700 px-2 py-0.5 rounded text-xs font-mono">
                      {e.employeeNumber || "—"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-gray-600">{e.city || "—"}</td>
                  <td className="px-4 py-3 text-gray-600">{e.employmentTypeLabel || "—"}</td>
                  <td className="px-4 py-3">
                    <span className="text-sm font-medium text-gray-900">{e.remainingLeaveDays ?? "—"}</span>
                    <span className="text-xs text-gray-400"> / {e.annualLeaveDays ?? "—"} gün</span>
                  </td>
                  <td className="px-4 py-3">
                    <span className={`px-2 py-1 rounded-full text-xs font-medium ${STATUS_COLORS[e.status || ""] || "bg-gray-100 text-gray-600"}`}>
                      {e.statusLabel || e.status || "—"}
                    </span>
                  </td>
                  <td className="px-4 py-3 text-right">
                    <button onClick={() => navigate(e.id)}
                      className="text-primary-600 hover:text-primary-800 text-xs font-medium">
                      Detay →
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default EmployeeList;
