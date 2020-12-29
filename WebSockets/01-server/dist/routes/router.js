"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = require("express");
const router = express_1.Router();
router.get('/mensajes', (req, res) => {
    res.json({
        ok: true,
        mensaje: 'Todo esta bien GET!!!'
    });
});
router.post('/mensajes', (req, res) => {
    res.json({
        ok: true,
        mensaje: 'Todo esta bien POST!!!'
    });
});
exports.default = router;
