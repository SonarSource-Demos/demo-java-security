# SonarQube CAG MCP Tools - Usage Directive (MUST FOLLOW)

**Always use CAG tools** for code analysis - do not rely on memory or text search alone.

**Before editing code** you MUST:
1. Call `get_guidelines` for project context
2. Locate code with `search_by_signature_patterns` or `search_by_body_patterns`
3. Read implementation with `get_source_code`

**When changing architecture or dependencies** you MUST:
- Check `get_current_architecture` and `get_intended_architecture`
- Analyze impact using:
  - `get_upstream_call_flow` / `get_downstream_call_flow` - trace method calls
  - `get_references` - find all usages
  - `get_type_hierarchy` - check inheritance
