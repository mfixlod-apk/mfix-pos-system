# MFIX Touch POS — Extension prototype

This folder contains the first, non-destructive Chrome Manifest V3 overlay prototype for the Yesh Invoice web app.

## Install on desktop Chrome / Edge

1. Download the repository ZIP from GitHub and extract it.
2. Open `chrome://extensions` (or `edge://extensions`).
3. Enable **Developer mode**.
4. Choose **Load unpacked** and select the `extension` folder.
5. Sign in to Yesh Invoice at https://user.yeshinvoice.co.il/.
6. A floating **פתיחת קופת MFIX** button should appear. Open it to use the touch layout. Choose **חזרה ליש חשבונית** to close the overlay and return to the untouched original page.

## Current prototype scope

- Injects only on `user.yeshinvoice.co.il` and Yesh Invoice subdomains.
- Keeps the original page intact; the POS opens as a full-screen extension iframe.
- Responsive RTL layout for desktop and tablet-sized screens.
- Category navigation, search, locally stored demo products, cart quantities and total.
- Product additions are stored in extension local storage.

## Important limitations — not for live sales yet

- Product cards are demo/sample data until a verified catalog sync is implemented.
- The “העברת פרטי העסקה לאתר” button only copies a JSON summary. It does **not** fill Yesh Invoice forms, issue an invoice/receipt, process a payment, change stock in Yesh Invoice, or open a cash drawer.
- No API credentials are requested or stored in the extension.
- No live transaction should be processed with this prototype.

## Next integration work

1. Inspect the authenticated Yesh Invoice screens and map the actual document-creation workflow and form fields.
2. Confirm the official API documentation and the account's API entitlement. Keep credentials on a secure backend, never in content scripts or browser storage.
3. Add a verified product/customer sync and an idempotent document-creation flow, initially against a test account.
4. Add explicit payment confirmation, printer integration and recovery handling.
5. Validate tablet-browser support. Standard Chrome on Android does not load desktop Chrome extensions; use a compatible browser or a dedicated WebView/PWA delivery for tablets.

## Files

- `manifest.json` — Manifest V3 metadata and site permissions.
- `content.js` — non-destructive floating launcher and overlay host.
- `pos.html`, `pos.css`, `pos.js` — standalone touch POS prototype.
