# Profile UI mapping

The existing restaurant dashboard history contains a `Profile.jsx` page. The new Profile-Service is kept separate from Restaurant-Service as requested.

The backend exposes the profile information needed for a typical restaurant profile form:

- Restaurant name
- Cuisine type
- Description
- Phone
- Email
- Website
- Address
- City / State / Postal code / Country
- Logo URL
- Opening time / Closing time
- Active status

If the final Profile screenshot contains additional fields, they can be added as a backward-compatible migration/versioned API change without moving the profile into Restaurant-Service.
