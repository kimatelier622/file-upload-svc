---
name: spec-driven-development
description: "Creates structured specifications, validates execution plans against specs, manages two-loop planning-to-execution workflows with decision gates, and captures provenance of AI-generated work. Use when building software with AI coding agents, when starting any non-trivial feature or change, when an agent keeps building the wrong thing, when you want repeatable and verifiable AI-assisted development, or when someone mentions spec-driven, SDD, specification first, or code is a side effect. Also use when the user wants to structure agent workflows, create specifications, establish decision gates between planning and execution, or capture provenance."
license: CC-BY-4.0
compatibility: Designed for Claude Code, Cursor, Windsurf, or any MCP-compatible agent
metadata:
  author: Kevin Ryan
  version: "1.1.0"
---

# Spec-Driven Development

Specifications are the primary artifact. Code is a generated side effect.

This skill teaches agents the full SDD workflow: how to write specifications,
how to execute them through a two-loop process with explicit decision gates,
and how to capture provenance of every execution.

## Core Concept

The specification carries the intent. The prompt is just the trigger.
If your prompt needs to explain the work, your spec is deficient.

The human spends cognitive budget on specifications. The agent spends compute
on execution. Cognitive budget replaces developer-hours as the primary
constraint.

## The Two Loops

SDD has two loops with nine steps. The first loop is creative work — this is
where cognitive budget is spent. The second loop is mechanical — it should be
boring.

### The Spec Loop (creative)

1. **Brief** — human intent as bullet points, rough shape. The brief is the
   spec for the spec. This is where you think.
2. **Spec draft** — agent expands the brief into a full specification. The
   spec is the precise, complete document an agent can execute without
   further context.
3. **Iterate spec** — review, refine, catch gaps. Rewriting a spec is
   iteration, not rework.
4. **Commit spec to main** — the spec is the artifact. It is done. It is
   reviewable. It is the source of truth.

### The Execution Loop (mechanical)

5. **Plan** — agent reads spec, produces an execution plan. The plan
   validates the spec against the current state of the codebase.
6. **Validate plan against spec** — THE CRITICAL DECISION GATE. Does the
   plan reveal spec bugs? If yes: fix the spec (return to step 3), discard
   the plan. Do not patch the plan. The plan is disposable. The spec is the
   artifact.
7. **Execute** — the prompt should be minimal: "Execute spec X." If you are
   adding context, caveats, or instructions, your spec has holes.
8. **Validate results** — mandatory checks defined in the spec. Every check
   is mandatory. Do not skip any.
9. **Write provenance** — document what happened. Which spec was executed,
   what plan was produced, what deviations occurred, what the outcome was.
   Overwrite the provenance file (do not append). This is not optional.
   Every spec execution produces a provenance record.

### The Step 6 Decision Gate

The plan reveals problems that would cost a full re-execution to fix if
caught later.

When the plan reveals a spec deficiency:

- Fix the spec, not the plan
- Discard the plan entirely
- Generate a fresh plan from the updated spec

When the plan has a bug but the spec is clear:

- Fix the plan with a minimal correction prompt
- Document the deviation

The whole execution should be three prompts: "create a plan," a brief
correction after plan review, and "execute." If it takes more, your spec
has holes or your plan review is not catching enough.

## Spec Format

A specification must be executable by a fresh agent with no prior conversation
context. Test this: can someone who has never seen your project execute from
the spec alone?

### Required Sections

- **Purpose** — one sentence. What this spec produces.
- **Prerequisites** — what the agent must load or verify before starting.
  Name specific skills, context files, or tools.
- **Context** — why this spec exists. Brief, not a novel.
- **Changes** — numbered steps. Each step is an instruction, not a wish.
  Be prescriptive about what, principled about how.
- **Out of Scope** — explicit boundaries. Without this, agents expand scope.
  They update documentation you did not ask for, refactor code that was not
  in the spec, and add features that seemed related.
- **Verification** — mandatory checks. Write: "Every check below is
  mandatory. Do not skip any." Include both positive tests (what must exist)
  and negative tests (what must not exist). If you cannot write a concrete
  check, you do not know what success looks like.
- **Branch** — the branch naming convention for this execution.
- **Provenance** — the file path where the provenance record for this
  execution must be written. Use a consistent convention, e.g.
  `specs/provenance/<category>/<spec-name>.provenance.md`. Overwrite
  semantics — each execution replaces the previous record. Use
  `git log --follow` to recover history.

### Spec Principles

State conventions explicitly. Never assume an agent knows a convention. If
provenance files should be overwritten not appended, say so. If tools must
return error strings not raise exceptions, say so.

Single source of truth — always. If you know two sources of truth is wrong,
fix it now. Do not defer unification to a "future spec."

Keep specs focused. If a spec has more than roughly ten changes, it probably
wants to be two specs.

See [the principles reference](references/principles.md) for the full set of
practitioner-discovered principles with explanations.

## The Four Types of Context

Every piece of context an agent loads falls into one of four categories.
When the executor is an AI agent, you cannot rely on tribal knowledge — you
must make all four types explicit, structured, and machine-readable.

| Type | Definition | Contains |
|------|-----------|----------|
| **Intent** | What needs to happen | Specifications, briefs, task definitions |
| **Method** | How to do it well | Skills, guides, frameworks, best practices |
| **Brand** | What it must look and sound like | Voice, design tokens, style constraints |
| **State** | What has already happened | Glossaries, continuity tracking, decision logs |

This maps to how anyone approaches work: what am I doing, how should I do it,
what are the guardrails, and what has already been done.

The agent is the orchestrator. All four context sources are peers — passive
resource providers. No context source calls another. The agent reads the spec,
sees "load the X skill," and the agent fetches it. The spec is instructions.
The context sources are the filing cabinets. The agent is the person walking
between them.

## Provenance

Every spec execution produces a provenance record. Provenance is a side
effect of the process, not extra work.

A provenance file documents:

- Which spec was executed
- What plan was produced
- What deviations occurred (and why)
- What the outcome was
- What was learned

Provenance files are overwritten on re-execution, not appended.
`git log --follow` on the provenance file gives the full evolution — spec
changes, execution changes, failures and fixes — a narrative arc built into
version control.

Deviations are not failures. Hidden deviations are. If the agent goes
off-spec, the provenance file is the place to be honest. Log deviations,
do not hide them.

## When NOT to Use SDD

Not everything is a spec. If the human is the entire loop — intent, creation,
and validation — a spec adds ceremony, not value.

Ask: "Is there work here that benefits from specification?" If yes, spec it.
If the human is the entire creative loop, just do the work. No spec, no
provenance. Do not cargo-cult the methodology.

Your entire solution does not need to be SDD. Apply it to the slice that
benefits from specification rigour — build pipelines, infrastructure, data
contracts, repeatable generation tasks. Ship the rest normally.

## Spec Anti-Patterns

See [the anti-patterns reference](references/anti-patterns.md) for detailed
descriptions. In summary, watch for:

- **The novel spec** — reads like a design document. Specs are instructions,
  not essays.
- **The implicit spec** — assumes the agent knows things it has not been told.
  "Follow our usual conventions" is not a requirement.
- **The mega-spec** — tries to do too much. Split it.
- **The unverifiable spec** — no verification section, or vague checks like
  "ensure it works."
- **The deferred debt spec** — knowingly creates a problem and adds "future
  spec" to fix it. If you know it is wrong now, fix it now.
- **The prompt-dependent spec** — only works if the execution prompt adds
  extra context.

## Applying SDD to an Existing Project

When adopting SDD on an existing codebase:

1. Start with one well-understood feature or infrastructure task.
2. Write a brief — bullet points capturing your intent.
3. Expand the brief into a full spec following the format above.
4. Execute in plan mode first. Review the plan. Fix the spec if needed.
5. Execute. Validate against the spec's mandatory checks.
6. Write the provenance file.
7. Review what you learned. The first spec is always the hardest. Each
   subsequent spec benefits from the patterns established by the ones
   before it.

Velocity compounds. The first spec takes the longest. The third is
dramatically faster. Each layer of specification, skill, and structured
context reduces the cognitive load for every subsequent task.

## Key Principles

See [the principles reference](references/principles.md) for the full set
with explanations. The most important:

- The spec is cheap, execution is expensive.
- If your prompt needs to explain the work, your spec is deficient.
- The bug is always in the spec.
- When the plan reveals a spec bug, fix the spec — not the plan.
- Deviations are not failures. Hidden deviations are.
