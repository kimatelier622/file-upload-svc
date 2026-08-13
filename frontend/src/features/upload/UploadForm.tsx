import { type ChangeEvent, type FormEvent, useState } from "react";
import { uploadFile, type UploadResponse } from "../../api/client";
import { uploadErrorMessage } from "./uploadErrorMessage";
import { UploadResult } from "./UploadResult";

const allowed = ["image/jpeg", "image/png", "application/pdf"] as const;
export function UploadForm({ token }: { token: string }) {
  const [file, setFile] = useState<File | null>(null);
  const [result, setResult] = useState<UploadResponse | null>(null);
  const [error, setError] = useState(""); const [uploading, setUploading] = useState(false);
  function chooseFile(event: ChangeEvent<HTMLInputElement>) { setFile(event.target.files?.[0] ?? null); setResult(null); setError(""); }
  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault(); setError(""); setResult(null);
    if (!token.trim()) return setError("Paste a valid Bearer JWT before uploading.");
    if (!file) return setError("Select exactly one non-empty file.");
    if (file.size > 10_485_760) return setError("The file exceeds the 10 MiB limit.");
    if (!allowed.includes(file.type as typeof allowed[number])) return setError("Only JPEG, PNG, and PDF files are allowed.");
    setUploading(true);
    try { setResult(await uploadFile(file, token.trim())); } catch (caught) { setError(uploadErrorMessage(caught instanceof Error ? caught.message : "")); } finally { setUploading(false); }
  }
  return <form className="upload-form" onSubmit={submit}><label className="file-picker"><input aria-label="Select file" type="file" accept="image/jpeg,image/png,application/pdf" onChange={chooseFile}/><span className="file-picker-icon">↑</span><strong>{file ? file.name : "Choose a file"}</strong><small>{file ? `${(file.size / 1024 / 1024).toFixed(2)} MB selected` : "JPEG, PNG, or PDF · Maximum 10 MB"}</small></label><button className="primary-button" type="submit" disabled={uploading}>{uploading ? "Uploading…" : "Upload securely"}</button>{error && <p className="form-error" role="alert">{error}</p>}{result && <UploadResult result={result} token={token.trim()}/>}</form>;
}
