import { useState } from "react";
import { downloadFile, type UploadResponse } from "../../api/client";
import { uploadErrorMessage } from "./uploadErrorMessage";

export function UploadResult({ result, token }: { result: UploadResponse; token: string }) {
  const [error, setError] = useState("");
  const [downloading, setDownloading] = useState(false);
  async function download() {
    setError(""); setDownloading(true);
    try {
      const blob = await downloadFile(result.access_url, token);
      const href = URL.createObjectURL(blob); const anchor = document.createElement("a");
      anchor.href = href; anchor.download = result.file_id; anchor.click(); URL.revokeObjectURL(href);
    } catch (caught) { setError(uploadErrorMessage(caught instanceof Error ? caught.message : "")); }
    finally { setDownloading(false); }
  }
  return <section className="result-card" aria-live="polite"><div><span className="eyebrow">Upload complete</span><strong>File secured and ready</strong></div><code>{result.file_id}</code><button className="secondary-button" type="button" onClick={download} disabled={downloading}>{downloading ? "Preparing download…" : "Download with JWT"}</button>{error && <p className="form-error" role="alert">{error}</p>}</section>;
}
