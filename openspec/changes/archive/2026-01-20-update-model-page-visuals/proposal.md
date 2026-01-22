# Change: Update model page visuals for readability

## Why

Model page hover state turns the row white, making text unreadable, and the create/edit/delete modals use a white
background that clashes with the dark list theme.

## What Changes

- Adjust model list hover styling so hovered rows maintain readable contrast in the dark theme.
- Align create/edit/delete modal visuals with the model page theme (background, text, borders).

## Impact

- Affected specs: model-management-ui
- Affected code: `frontend/src/pages/model-page/model-page.css`,
  `frontend/src/components/model-form-modal/model-form-modal.css`, `frontend/src/index.css` or Ant Design theme
  overrides (if needed)
