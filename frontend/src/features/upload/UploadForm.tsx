import { useState } from "react";
import { uploadFile, type UploadResponse } from "../../api/client";
import { uploadErrorMessage } from "./uploadErrorMessage";
import { UploadResult } from "./UploadResult";

const allowed = ["image/jpeg", "image/png", "application/pdf"];
export function UploadForm({ token }: { token: string }) {
  const [file, setFile] = useState<File>(); const [result, setResult] = useState<UploadResponse>(); const [error, setError] = useState("");
  async function submit(event: React.FormEvent) { event.preventDefault(); setError(""); setResult(undefined);
    if (!file) return setError("Select exactly one non-empty file.");
    if (file.size > 10_485_760) return setError("The file exceeds the 10 MiB limit.");
    if (!allowed.includes(file.type)) return setError("Only JPEG, PNG, and PDF files are allowed.");
    try { setResult(await uploadFile(file, token)); } catch (e) { setError(uploadErrorMessage(e instanceof Error ? e.message : "")); }
  }
  return <form onSubmit={submit}><label>File<input aria-label="File" type="file" accept="image/jpeg,image/png,application/pdf" onChange={e => setFile(e.target.files?.[0])}/></label><button type="submit">Upload</button>{error && <p role="alert">{error}</p>}{result && <UploadResult result={result}/>}</form>;
}
