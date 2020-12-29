import {Router, Request, Response} from 'express';

const router = Router();

router.get('/mensajes', (req: Request, res: Response) => {
    res.json({
        ok: true,
        mensaje: 'Todo esta bien GET!!!'
    });
});

router.post('/mensajes', (req: Request, res: Response) => {
    res.json({
        ok: true,
        mensaje: 'Todo esta bien POST!!!'
    });
});

export default router;