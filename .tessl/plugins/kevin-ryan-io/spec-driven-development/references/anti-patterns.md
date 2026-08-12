# Spec Anti-Patterns

These anti-patterns were identified through repeated encounters during
real SDD practice. Each one describes a failure mode, explains why it
fails, and gives a concrete fix.

## The Novel Spec

**Symptom:** Reads like a design document — pages of context and rationale
with requirements buried in prose.

**Why it fails:** Agents extract instructions from structure, not narrative.
Buried requirements get missed. Context paragraphs consume tokens without
directing action.

**Fix:** Separate context from instructions. The Context section provides
background. The Changes section provides numbered steps. Requirements are
instructions, not observations.

## The Implicit Spec

**Symptom:** Assumes the agent knows things it has not been told. Contains
phrases like "follow our usual conventions" or "do it the same way as last
time."

**Why it fails:** A fresh agent has no prior conversation context. Implicit
knowledge is invisible knowledge. Conventions that exist in one part of the
codebase but are not stated in the spec will not be followed.

**Fix:** State every convention explicitly. If provenance files should be
overwritten not appended, say so. If tool names follow a prefix convention,
list them. Test: can a fresh agent with no prior conversation execute from
the spec alone?

## The Mega-Spec

**Symptom:** Tries to do too much. More than roughly ten discrete changes.
Covers multiple concerns that could be independent.

**Why it fails:** Large specs produce large plans that are hard to review.
The step 6 decision gate becomes less effective because there is too much
to validate in a single pass. Failures in one area contaminate the whole
execution.

**Fix:** Split it. If a spec has more than ten changes, it probably wants
to be two specs. Each spec should have a single, coherent purpose.

## The Unverifiable Spec

**Symptom:** No verification section, or vague checks like "ensure it
works" or "test that everything is correct."

**Why it fails:** If you cannot write a concrete check, you do not know
what success looks like. The agent has no way to validate its own output.
You have no way to validate the agent's output.

**Fix:** Write specific, enumerable checks. "File X exists at path Y."
"Running command Z produces exit code 0." "The output contains exactly N
items." Include both positive tests (what must exist) and negative tests
(what must not exist).

## The Deferred Debt Spec

**Symptom:** Knowingly creates a problem and adds "future spec" to fix it.
Phrases like "we will address this later" or "to be unified in a
subsequent spec."

**Why it fails:** Deferred debt compounds. The "future spec" inherits the
mess and has to work around it. You are spending cognitive budget twice —
once to create the workaround, once to undo it.

**Fix:** If you know it is wrong now, fix it now. If fixing it makes the
current spec too large, that is a signal to redesign the approach, not to
defer the problem.

## The Prompt-Dependent Spec

**Symptom:** Only works if the execution prompt adds extra context.
The person executing the spec needs to know things that are not in the
spec — environment setup, which branch to start from, what to do when
a specific tool is unavailable.

**Why it fails:** The spec is supposed to be the complete handoff. If the
prompt needs to explain the work, the spec is deficient. This also means
the spec cannot be re-executed by a different person or at a different
time without the original prompt context.

**Fix:** Test with a fresh agent. Open a new session with no prior
conversation. Give it only the spec and see if it can produce a valid
plan. If it cannot, the missing context belongs in the spec.
