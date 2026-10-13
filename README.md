# MFIX POS System

Hebrew RTL point-of-sale and small-business management prototype.

## Included
- Dashboard: daily sales, estimated gross profit, low-stock alerts, recent transactions.
- POS: product search, cart, quantity controls, discount, simulated payment method, checkout and stock decrement.
- Products: add/edit products, SKU, barcode, category, cost, selling price and stock.
- Customers: customer list and add-customer form.
- Reports: sales history, estimated gross profit and stock-at-cost valuation.
- Settings: business name, VAT display setting and JSON backup export.
- Responsive RTL layout for desktop, tablet and mobile.
- Local browser persistence through localStorage.

## Run locally

```bash
npm install
npm run dev
```

## Prototype limitations
This is not a production fiscal POS system. Demo sales do not charge cards or issue legally valid tax documents. Data is stored in the current browser only and does not sync across devices. VAT settings are informational. Printer/cash-drawer, barcode hardware, supplier purchasing, refunds, user authentication, cloud database, audit log, "Yesh Invoice" and payment-provider integrations are not connected yet.

## Next milestones
1. Add automated build checks and validate cart/inventory flows.
2. Add Supabase database, authentication, roles and cross-device sync.
3. Add supplier records, purchase orders, goods receiving, stock adjustments and refunds.
4. Integrate thermal printers/cash drawer and barcode input on supported hardware.
5. Integrate "Yesh Invoice" using verified API documentation and server-side secrets.
6. Integrate a payment provider in test mode before any live payments.

Never store API secrets in browser code.