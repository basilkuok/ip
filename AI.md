# A-AiAssisted: AI Usage Log

This file records how AI tools were used for the **A-AiAssisted** increment.

## Tools used
- ChatGPT (GPT-5.2) for code quality review and refactoring.

## What AI helped with
- Identified the main readability hotspots based on the course guidelines on code quality (in SE-textbook). For examples, guidelines on SLAP, guard clauses, reducing nesting, and clearer naming.
- Proposed a small, behavior-preserving refactor plan before making changes to the code.
- Drafted refactor patches and test refactors while keeping changes minimal.

## Changes made to Jarvis intelligent chatbot
- Refactored `Storage.parseTaskLine(...)` to follow SLAP by extracting
  type-specific parsing into helper methods.
- Added defensive `assert` statements in `Storage` for non-null inputs.
- Refactored `StorageTest` to improve readability by extracting assertion
  helpers and using `assertThrows` for exception expectations.

## Observations
- AI is most effective for:
  - spotting opportunities to extract helper methods (SLAP),
  - suggesting guard-clause patterns,
  - making tests more readable without changing behavior.
- AI is less effective when:
  - understand and synthesize higher-level abstraction in the architectural design of the Jarvis intelligent chatbot. Need human to make decision in some critical design choices and interpretations.

## Time saved
- Estimated time saved: ~30–60 minutes (faster refactor + fewer iterations).

