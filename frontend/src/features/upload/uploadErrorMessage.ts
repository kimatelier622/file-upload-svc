export function uploadErrorMessage(status: string): string {
  return ({ "400": "Select exactly one non-empty file.", "401": "Your session is not authorized to upload.", "413": "The file exceeds the 10 MiB limit.", "415": "Only JPEG, PNG, and PDF files are allowed." } as Record<string, string>)[status] ?? "The upload could not be completed.";
}
