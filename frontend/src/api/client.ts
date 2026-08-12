export type UploadResponse = { file_id: string; access_url: string };

export async function uploadFile(file: File, token: string): Promise<UploadResponse> {
  const form = new FormData(); form.append("file", file);
  const response = await fetch("/api/upload", { method: "POST", headers: { Authorization: `Bearer ${token}` }, body: form });
  if (!response.ok) throw new Error(String(response.status));
  return response.json() as Promise<UploadResponse>;
}
