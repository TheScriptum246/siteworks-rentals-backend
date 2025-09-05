-- Insert default roles
INSERT INTO roles (name) VALUES ('ROLE_CLIENT');
INSERT INTO roles (name) VALUES ('ROLE_STAFF');

-- Insert sample equipment
-- EXCAVATORS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('CAT 320 Excavator', 'Mid-size hydraulic excavator perfect for construction and demolition work', 450.00, 'EXCAVATORS', 'Operating weight: 20,000 kg, Max digging depth: 6.7m, Engine: 122 HP', true, null, NOW()),
                                                                                                                      ('Komatsu PC200 Excavator', 'Reliable excavator with excellent fuel efficiency and power', 425.00, 'EXCAVATORS', 'Operating weight: 19,900 kg, Max digging depth: 6.5m, Engine: 148 HP', true, null, NOW()),
                                                                                                                      ('JCB JS130 Compact Excavator', 'Compact excavator ideal for tight spaces and urban construction', 285.00, 'EXCAVATORS', 'Operating weight: 13,000 kg, Max digging depth: 5.8m, Engine: 74 HP', true, null, NOW());

-- BULLDOZERS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('CAT D6T Bulldozer', 'Medium bulldozer with excellent grading and dozing capabilities', 525.00, 'BULLDOZERS', 'Operating weight: 18,500 kg, Blade capacity: 3.8m³, Engine: 215 HP', true, null, NOW()),
                                                                                                                      ('Komatsu D51EX Bulldozer', 'Versatile bulldozer for earthmoving and site preparation', 485.00, 'BULLDOZERS', 'Operating weight: 16,800 kg, Blade capacity: 3.2m³, Engine: 168 HP', true, null, NOW());

-- CRANES
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('Liebherr LTM 1050 Mobile Crane', 'All-terrain mobile crane for lifting and placing heavy materials', 850.00, 'CRANES', 'Max lifting capacity: 50 tons, Max boom length: 48m, Engine: 367 HP', true, null, NOW()),
                                                                                                                      ('Grove RT540E Rough Terrain Crane', 'Compact rough terrain crane for construction sites', 650.00, 'CRANES', 'Max lifting capacity: 35 tons, Max boom length: 31m, Engine: 173 HP', true, null, NOW());

-- COMPACTORS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('CAT CB24 Vibratory Compactor', 'Double drum vibratory compactor for asphalt and soil compaction', 195.00, 'COMPACTORS', 'Operating weight: 2,400 kg, Drum width: 1.2m, Vibration frequency: 67 Hz', true, null, NOW()),
                                                                                                                      ('Bomag BW120 Compactor', 'Single drum compactor ideal for trenches and confined areas', 165.00, 'COMPACTORS', 'Operating weight: 1,200 kg, Drum width: 0.8m, Vibration frequency: 90 Hz', true, null, NOW());

-- GENERATORS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('CAT C15 500kW Generator', 'High-capacity diesel generator for large construction sites', 285.00, 'GENERATORS', 'Power output: 500kW, Fuel capacity: 1,500L, Runtime: 24 hours', true, null, NOW()),
                                                                                                                      ('Caterpillar C9 200kW Generator', 'Mid-range generator perfect for medium construction projects', 165.00, 'GENERATORS', 'Power output: 200kW, Fuel capacity: 750L, Runtime: 18 hours', true, null, NOW()),
                                                                                                                      ('Honda EU70is Portable Generator', 'Compact portable generator for small tools and lighting', 45.00, 'GENERATORS', 'Power output: 7kW, Fuel capacity: 40L, Runtime: 8 hours', true, null, NOW());

-- SCAFFOLDING
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('Layher Allround Scaffolding System', 'Complete modular scaffolding system for building construction', 85.00, 'SCAFFOLDING', 'Max height: 40m, Load capacity: 600 kg/m², Includes platforms and guardrails', true, null, NOW()),
                                                                                                                      ('Kwikstage Scaffolding Kit', 'Versatile scaffolding system for various construction needs', 65.00, 'SCAFFOLDING', 'Max height: 30m, Load capacity: 500 kg/m², Quick assembly system', true, null, NOW());

-- TOOLS
INSERT INTO equipment (name, description, daily_rate, category, specifications, available, image_url, created_at) VALUES
                                                                                                                      ('Hilti TE 3000-AVR Breaker', 'Heavy-duty demolition hammer for concrete breaking', 95.00, 'TOOLS', 'Impact energy: 65J, Weight: 28kg, SDS-Max chuck system', true, null, NOW()),
                                                                                                                      ('Makita DHR263Z Rotary Hammer', 'Cordless rotary hammer for drilling and light demolition', 35.00, 'TOOLS', 'Impact energy: 3.2J, Battery: 36V, SDS-PLUS chuck system', true, null, NOW()),
                                                                                                                      ('STIHL TS420 Cut-off Saw', 'Professional cut-off saw for concrete, stone, and metal cutting', 55.00, 'TOOLS', 'Engine: 66.7cc, Cutting depth: 125mm, Blade diameter: 350mm', true, null, NOW()),
                                                                                                                      ('DeWalt DW735 Thickness Planer', 'Professional thickness planer for wood processing', 75.00, 'TOOLS', 'Cutting width: 330mm, Max depth: 3mm, Motor: 1800W', true, null, NOW());

