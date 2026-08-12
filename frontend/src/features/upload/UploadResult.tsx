import type { UploadResponse } from "../../api/client";
export function UploadResult({ result }: { result: UploadResponse }) { return <p>Uploaded: {result.file_id} — <a href={result.access_url}>Access file</a></p>; }
