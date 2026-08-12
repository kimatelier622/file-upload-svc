# SDD Principles Reference

Every one of these principles was learned by hitting the problem it prevents.
They are listed in order of discovery, not importance — though the first few
are the most fundamental.

## The Spec Is Cheap, Execution Is Expensive

Rewriting a spec and discarding a plan feels like rework. It is iteration.
The spec is a few hundred words. Execution is files, tests, commits, reviews.
Always invest in getting the spec right before executing.

## If Your Prompt Needs to Explain the Work, Your Spec Is Deficient

The execution prompt should be: "Execute spec X." If you are adding context,
caveats, or instructions, your spec has holes. Fix the spec, not the prompt.

## Always Execute in Plan Mode First

The plan phase validates the spec against the current state of the codebase.
Catch problems in the spec during planning, not during execution. Never skip
straight to execution.

## When the Plan Reveals a Spec Bug, Fix the Spec — Not the Plan

If the plan exposes ambiguity, missing requirements, or implicit assumptions,
these are spec bugs. Update the spec, discard the plan, regenerate. The plan
is disposable. The spec is the artifact.

## Do Not Burn Cognitive Budget on Error Recovery

If the agent encounters issues during execution, the solution is to debug and
improve the specification, not manually fix outputs. Your cognitive budget is
better spent on spec quality than on patching broken execution.

## The Bug Is Always in the Spec

When execution produces wrong results, look at the spec first. Ambiguity,
missing requirements, and implicit assumptions are spec bugs, not agent bugs.
The agent that skipped plan approval and went straight to execution? That was
a spec bug — the workflow document did not explicitly require plan approval
before execution.

## State Conventions Explicitly

Never assume an agent knows a convention. If provenance files should be
overwritten not appended, say so. If tools must return error strings not raise
exceptions, say so. If a naming pattern exists in one part of the project,
it must be documented for agents working in other parts.

## Single Source of Truth — Always. Never Defer Unification

If you know two sources of truth is wrong, fix it now. Do not defer to a
"future spec." Having design tokens defined in both a JSON file and hardcoded
in markdown is wrong on sight. Unify before executing.

## Out of Scope Prevents Gold-Plating

Without explicit boundaries, agents expand scope. They update documentation
you did not ask for, refactor code that was not in the spec, and add features
that seemed related. Every spec needs an explicit "Out of Scope" section.

## Mandatory Means Mandatory

Plans will reduce verification checklists. Write: "Every check below is
mandatory. Do not skip any." Include positive and negative tests. If the spec
says 14 checks, the plan must have 14 checks.

## Provenance Is a Side Effect, Not Extra Work

Provenance files are generated as part of spec execution. They cost almost
nothing and become invaluable as audit trails, case studies, and primary
source material. The best documentation is a side effect of the process.

## The Brief Is the Spec for the Spec

New layer: brief → spec → plan → execution. The brief captures human intent
as bullet points. The spec is the precise, complete document an agent can
execute. This distinction matters because the brief is where you think, the
spec is where you hand off.

## If You Are Not Sure About the Spec, Execute Two and Pick the Winner

When reviewing a spec and you cannot tell whether it is good enough — execute
two versions on different branches and compare the outputs. The spec is cheap.
Execution is cheap. The delta between results tells you exactly what matters
in the spec and what was noise. Do not theorise about spec quality — run the
experiment.

## The Whole Execution Is Three Prompts

Prompt 1: "create a plan." Prompt 2: a brief correction after plan review.
Prompt 3: "execute." Total cognitive budget spent: reviewing the plan and
writing a few sentences. If your execution takes more than three prompts,
your spec has holes or your plan review is not catching enough.

## Plan Review Earns Its Keep on the First Run

Concrete bugs caught in sixty seconds of reading the plan, fixed with one
sentence in the correction prompt. The step 6 decision gate is not ceremony;
it is where you catch the bugs that would cost a whole re-execution to fix.

## Log Deviations, Do Not Hide Them

If execution cannot follow the spec exactly (environment constraints, missing
tools, changed dependencies), log the deviation explicitly in the provenance
file. Deviations are not failures — hidden deviations are.

## Some Verification Needs Eyes, Not Checks

Not everything can be automated. A verification checklist can confirm that a
file exists, that LaTeX environments are defined, that CSS rules are present.
But it cannot tell you whether the visual output looks right. The spec defines
the structure; the human confirms the aesthetics. When visual tweaks are
needed, they go back into the spec and are re-executed — the spec stays the
source of truth even for intuitive judgements.

## Not Everything Is a Spec

If the human is the entire loop — intent, creation, and validation — a spec
adds ceremony, not value. Apply SDD to the slice that benefits from
specification rigour. Ship the rest normally. The value is in knowing which
part benefits, not in making everything a spec.

## Velocity Compounds

Each layer of specification, skill, and structured context makes the next
layer faster. The first spec takes the longest. By the third, agents are
producing working output on the first attempt. The investment in structured
context pays back faster than expected because each artifact reduces the
cognitive load for every subsequent task.
