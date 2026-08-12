import { describe, expect, it } from "vitest";
import { uploadErrorMessage } from "./uploadErrorMessage";

describe("upload errors", () => {
  it("maps contract rejection status codes safely", () => {
    expect(uploadErrorMessage("401")).toContain("not authorized");
    expect(uploadErrorMessage("413")).toContain("10 MiB");
    expect(uploadErrorMessage("415")).toContain("JPEG");
  });
});
